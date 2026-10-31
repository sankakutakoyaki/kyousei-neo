package com.kyouseipro.neo.domain.operations.dispatch.result;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;
import com.kyouseipro.neo.domain.business.order.history.OrderHistoryRepository;
import com.kyouseipro.neo.sql.model.TableMeta;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkResultService {
    private final SqlRepository sql;
    private final BaseSqlRepository base;
    private final OwnOfficeContextService officeContext;
    private final OrderHistoryRepository histories;
    public static final TableMeta TABLE = new TableMeta("order_work_results", "workResultId", "state", "version");
    public record Request(Integer assignmentVersion, Integer resultVersion, String resultType,
                          String reason, LocalDate workDate, UUID requestId) {}

    private static final String DETAIL_SQL = """
        SELECT da.dispatch_assignment_id, da.version AS assignment_version, da.daily_crew_id,
            CONVERT(char(10), dc.work_date, 23) AS work_date, wr.work_result_id, wr.version AS result_version,
            wr.result_type, wr.reason
        FROM dbo.dispatch_assignments da
        JOIN dbo.daily_crews dc ON dc.daily_crew_id=da.daily_crew_id AND dc.state=0
        JOIN dbo.orders o ON o.order_id=da.order_id AND o.state<>9
        LEFT JOIN dbo.order_work_results wr ON wr.dispatch_assignment_id=da.dispatch_assignment_id AND wr.work_date=dc.work_date AND wr.state=0
        WHERE da.order_id=? AND da.dispatch_assignment_id=? AND da.state=0
        AND EXISTS (SELECT 1 FROM dbo.daily_crew_members dcm
            WHERE dcm.daily_crew_id=dc.daily_crew_id AND dcm.employee_id=? AND dcm.state=0)
        """;

    private long employee(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) throw new AccessDeniedException("ログイン情報を確認してください。");
        Long id = officeContext.getEmployeeId(auth);
        if (id == null || id <= 0) throw new AccessDeniedException("従業員情報を確認できません。");
        return id;
    }
    private Map<String,Object> assigned(long orderId, long assignmentId, long employeeId) {
        var rows = sql.selectMap(DETAIL_SQL, List.of(orderId, assignmentId, employeeId));
        if (rows.isEmpty()) throw new AccessDeniedException("担当外または解除済みの伝票です。開き直してください。");
        return rows.get(0);
    }
    @Transactional(readOnly = true)
    public Map<String,Object> detail(Authentication auth, long orderId, long assignmentId) {
        return assigned(orderId, assignmentId, employee(auth));
    }

    @Transactional
    public Map<String,Object> save(Authentication auth, long orderId, long assignmentId, Request req) {
        long employeeId = employee(auth);
        if (req == null || req.requestId() == null || !Set.of("COMPLETED", "DELIVERY_FAILED", "POSTPONED").contains(req.resultType() == null ? "" : req.resultType()))
            throw new BusinessException("作業結果を選択して開き直してください。");
        String reason = req.reason() == null ? "" : req.reason().trim();
        if (reason.length()>1000) throw new BusinessException("理由・備考は1000文字以内で入力してください。");
        var initial = assigned(orderId, assignmentId, employeeId);
        Object crewId = initial.get("dailyCrewId");
        // 配車移動と同じく班→割当の順にロックし、取得後に所属・担当を再確認する。
        var crew = sql.selectMap("SELECT daily_crew_id FROM dbo.daily_crews WITH (UPDLOCK,HOLDLOCK) WHERE daily_crew_id=? AND state=0", List.of(crewId));
        if (crew.isEmpty()) throw new BusinessException("班が変更されています。開き直してください。");
        sql.selectMap("SELECT dispatch_assignment_id FROM dbo.dispatch_assignments WITH (UPDLOCK,HOLDLOCK) WHERE dispatch_assignment_id=?", List.of(assignmentId));
        var current = assigned(orderId, assignmentId, employeeId);
        if (!Objects.equals(crewId, current.get("dailyCrewId"))) throw new BusinessException("配車が変更されています。開き直してください。");
        if (req.workDate() == null || !req.workDate().toString().equals(current.get("workDate").toString()))
            throw new BusinessException("訪問日が変更されています。開き直してください。");
        var duplicate = sql.selectMap("SELECT dispatch_assignment_id, work_date, result_type, reason FROM dbo.order_work_results_log WHERE request_id=?", List.of(req.requestId().toString()));
        if (!duplicate.isEmpty()) {
            var old = duplicate.get(0);
            String oldDate = old.get("workDate").toString();
            if (((Number)old.get("dispatchAssignmentId")).longValue()!=assignmentId
                || !Objects.equals(old.get("resultType"), req.resultType()) || !Objects.equals(old.get("reason"), reason)
                || !Objects.equals(oldDate, req.workDate().toString()))
                throw new BusinessException("登録要求が重複しています。開き直してください。");
            return current;
        }
        if (!Objects.equals(current.get("assignmentVersion"), req.assignmentVersion()))
            throw new BusinessException("配車が更新されています。開き直してください。");
        boolean correction = current.get("workResultId") != null;
        if (!Objects.equals(current.get("resultVersion"), req.resultVersion()))
            throw new BusinessException("作業結果が更新されています。開き直してください。");
        if ((correction || !"COMPLETED".equals(req.resultType())) && reason.isBlank())
            throw new BusinessException(correction ? "訂正理由を入力してください。" : "理由を入力してください。");
        String editor = officeContext.getAccount(auth);
        if (editor == null || editor.isBlank()) throw new AccessDeniedException("ログイン情報を確認してください。");
        Map<String,Object> data = new LinkedHashMap<>();
        data.put("resultType", req.resultType()); data.put("reason", reason);
        data.put("resultEmployeeId", employeeId); data.put("requestId", req.requestId().toString());
        long id;
        if (correction) {
            id=((Number)current.get("workResultId")).longValue();
            data.put("workResultId", id); data.put("version", current.get("resultVersion"));
            base.update(TABLE, data, editor);
        } else {
            data.put("dispatchAssignmentId", assignmentId); data.put("workDate", Date.valueOf(req.workDate())); data.put("state", 0);
            id=base.insert(TABLE, data, editor);
        }
        histories.recordWorkResult(id, correction, editor);
        return assigned(orderId, assignmentId, employeeId);
    }
}
