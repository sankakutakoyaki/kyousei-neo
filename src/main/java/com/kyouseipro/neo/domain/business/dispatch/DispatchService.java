package com.kyouseipro.neo.domain.business.dispatch;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
public class DispatchService {
    private final SqlRepository sql;
    private final DispatchLogSqlProvider logProvider;
    private final jakarta.servlet.http.HttpServletRequest request;
    private static final String SUMMARY = """
        SELECT o.order_id, o.request_number, o.title, o.full_address, o.visit_date, o.visit_time,
            o.version, o.state, COALESCE(v.version, 0) AS dispatch_version,
            c.name AS company_name,
            (SELECT STRING_AGG(CAST(a.employee_name AS NVARCHAR(MAX)), N'、') FROM order_dispatch_assignments a
             WHERE a.order_id=o.order_id AND a.role='DELIVERY' AND a.cancelled_at IS NULL) AS delivery_names,
            (SELECT STRING_AGG(CAST(a.employee_name AS NVARCHAR(MAX)), N'、') FROM order_dispatch_assignments a
             WHERE a.order_id=o.order_id AND a.role='INSTALL' AND a.cancelled_at IS NULL) AS install_names
        FROM orders o
        LEFT JOIN companies c ON c.company_id=o.prime_constractor_id
        LEFT JOIN order_dispatch_versions v ON v.order_id=o.order_id
        """;

    public List<Map<String,Object>> list(Map<String,Object> p) {
        LocalDate from = day(p.get("dateFrom")), to = day(p.get("dateTo"));
        if (to.isBefore(from) || to.isAfter(from.plusDays(92)))
            throw new BusinessException("検索期間は開始日から93日以内にしてください。");
        boolean undated = Boolean.TRUE.equals(p.get("includeUndated"));
        return sql.selectMap(SUMMARY + " WHERE o.state IN (0,2) AND ((o.visit_date >= ? AND o.visit_date < ?)"
            + (undated ? " OR o.visit_date IS NULL" : "") + ") ORDER BY o.visit_date, o.visit_time, o.order_id",
            List.of(Date.valueOf(from), Date.valueOf(to.plusDays(1))));
    }
    public List<Map<String,Object>> employees() {
        return sql.selectMap("""
            SELECT e.employee_id, e.full_name, e.category, c.name AS company_name
            FROM employees e LEFT JOIN companies c ON c.company_id=e.company_id
            WHERE e.state=0 AND e.category IN (1,2,3)
              AND (ISNULL(e.company_id,0)=0 OR c.state=0)
            ORDER BY e.category, c.name, e.full_name, e.employee_id
            """, List.of());
    }
    @Transactional
    public Map<String,Object> detail(Map<String,Object> p) {
        long id = positiveId(p.get("orderId"));
        lock(id);
        var rows = sql.selectMap(SUMMARY + " WHERE o.order_id=?", List.of(id));
        var result = new LinkedHashMap<>(rows.get(0));
        result.put("assignments", assignments(id));
        result.put("history", sql.selectMap("""
            SELECT role, employee_name, assigned_at, assigned_by, cancelled_at, cancelled_by
            FROM order_dispatch_assignments WHERE order_id=? ORDER BY assignment_id DESC
            """, List.of(id)));
        return result;
    }
    private Map<String,Object> lock(long id) {
        var rows = sql.selectMap("SELECT order_id, version, state FROM orders WITH (UPDLOCK,HOLDLOCK) WHERE order_id=? AND state IN (0,2)", List.of(id));
        if (rows.isEmpty()) throw new BusinessException("受注が存在しないか削除されています。");
        return rows.get(0);
    }
    private List<Map<String,Object>> assignments(long id) {
        return sql.selectMap("SELECT employee_id, role, employee_name FROM order_dispatch_assignments WHERE order_id=? AND cancelled_at IS NULL", List.of(id));
    }
    public record Assignment(long employeeId, String role) { }
    public static Set<Assignment> validateAssignments(Object input) {
        if (!(input instanceof List<?> rows) || rows.size() > 100)
            throw new BusinessException("担当者は合計100名以内で指定してください。");
        Set<Assignment> result = new LinkedHashSet<>();
        for (Object row : rows) {
            if (!(row instanceof Map<?,?> p)) throw new BusinessException("担当者の指定が不正です。");
            String role = String.valueOf(p.get("role"));
            if (!Set.of("DELIVERY", "INSTALL").contains(role)) throw new BusinessException("配送・工事の区分が不正です。");
            if (!result.add(new Assignment(positiveId(p.get("employeeId")), role)))
                throw new BusinessException("同じ区分に担当者が重複しています。");
        }
        return result;
    }
    @Transactional
    public void save(Map<String,Object> p) {
        // 端末申告が欠落した要求は拒否。スマホ側も共通APIガードで管理者だけに制限する。
        if (!(p.get("mobile") instanceof Boolean)) throw new BusinessException("配車画面を開き直してください。");
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new AccessDeniedException("認証が必要です。");
        String userAgent = Objects.toString(request.getHeader("User-Agent"), "").toLowerCase(Locale.ROOT);
        boolean mobile = Boolean.TRUE.equals(p.get("mobile")) || "?1".equals(request.getHeader("Sec-CH-UA-Mobile"))
            || userAgent.matches(".*(android|iphone|ipad|ipod|mobile).*");
        if (mobile && auth.getAuthorities().stream().noneMatch(a ->
                Set.of("APPROLE_admin", "APPROLE_master", "APPROLE_leader").contains(a.getAuthority())))
            throw new AccessDeniedException("スマホでの配車編集には管理権限が必要です。");
        long id = positiveId(p.get("orderId"));
        Set<Assignment> desired = validateAssignments(p.get("assignments"));
        var order = lock(id);
        if (((Number)order.get("state")).intValue() != 0) throw new BusinessException("完了済みの受注は配車を変更できません。");
        sameVersion(order.get("version"), p.get("version"));
        var versions = sql.selectMap("SELECT version FROM order_dispatch_versions WITH (UPDLOCK,HOLDLOCK) WHERE order_id=?", List.of(id));
        Object version = versions.isEmpty() ? 0 : versions.get(0).get("version");
        sameVersion(version, p.get("dispatchVersion"));
        Map<Long,String> eligible = new HashMap<>();
        for (var e : employees()) eligible.put(((Number)e.get("employeeId")).longValue(), String.valueOf(e.get("fullName")));
        Set<Assignment> old = new HashSet<>();
        for (var a : assignments(id)) old.add(new Assignment(((Number)a.get("employeeId")).longValue(), a.get("role").toString()));
        // 無効になった担当者は既存割り当ての維持・解除のみ許可する。
        for (var a : desired) if (!old.contains(a) && !eligible.containsKey(a.employeeId()))
            throw new BusinessException("選択した担当者が削除・無効化されています。開き直してください。");
        if (old.equals(desired)) return;
        int nextVersion = ((Number) version).intValue() + 1;
        for (var a : old) if (!desired.contains(a)) updateWithLog("""
            UPDATE order_dispatch_assignments SET cancelled_at=SYSDATETIME(), cancelled_by=?
            %s WHERE order_id=? AND role=? AND employee_id=? AND cancelled_at IS NULL;
            """, List.of(auth.getName(), id, a.role(), a.employeeId()), "UPDATE", auth.getName(), nextVersion);
        for (var a : desired) if (!old.contains(a)) updateWithLog("""
            INSERT order_dispatch_assignments(order_id, employee_id, role, employee_name, assigned_by)
            %s VALUES (?,?,?,?,?);
            """, List.of(id, a.employeeId(), a.role(), eligible.get(a.employeeId()), auth.getName()), "INSERT", auth.getName(), nextVersion);
        if (versions.isEmpty()) sql.update("INSERT order_dispatch_versions(order_id,version) VALUES (?,1)", List.of(id));
        else sql.update("UPDATE order_dispatch_versions SET version=version+1 WHERE order_id=?", List.of(id));
    }
    private void updateWithLog(String statement, List<Object> params, String action, String editor, int version) {
        String tableVar = "@DispatchRows";
        String batch = logProvider.buildLogTable(tableVar)
            + statement.formatted(logProvider.buildOutput() + " INTO " + tableVar)
            + logProvider.buildInsertLog(tableVar, action);
        var values = new ArrayList<Object>(params);
        values.addAll(logProvider.buildLogParams(Map.of("editor", editor, "dispatchVersion", version), action));
        sql.update(batch, values);
    }
    private static void sameVersion(Object actual, Object expected) {
        if (expected == null || !String.valueOf(actual).equals(expected.toString()))
            throw new BusinessException("受注または配車が更新されています。開き直して確認してください。");
    }
    private static long positiveId(Object value) {
        try { long id = Long.parseLong(String.valueOf(value)); if (id > 0) return id; }
        catch (RuntimeException ignored) { }
        throw new BusinessException("IDの指定が不正です。");
    }
    private static LocalDate day(Object value) {
        try { return LocalDate.parse(String.valueOf(value)); }
        catch (RuntimeException e) { throw new BusinessException("検索日を正しく入力してください。"); }
    }
}
