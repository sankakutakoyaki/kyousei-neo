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
        throw new BusinessException("配車は運行メニューの新しい画面で登録してください。");
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
