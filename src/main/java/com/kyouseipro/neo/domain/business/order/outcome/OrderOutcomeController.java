package com.kyouseipro.neo.domain.business.order.outcome;

import java.util.*;
import com.kyouseipro.neo.common.enums.code.*;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import com.kyouseipro.neo.security.OrderScopeGuard;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.domain.operations.dispatch.result.WorkResultService;
import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.response.SimpleResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order-outcomes")
public class OrderOutcomeController {
    private final OrderScopeGuard guard;
    private final SqlRepository sql;
    private final OrderOutcomeService outcomes;
    private final WorkResultService results;
    private final OwnOfficeContextService context;
    public record Request(Integer version,String type,String reason,String cancelReason,LocalDate workDate,UUID requestId) {}
    @GetMapping("/{id}")
    public Map<String,Object> detail(Authentication auth,@PathVariable long id) {
        var order=new LinkedHashMap<>(guard.require(auth,id,true));
        var reports=sql.selectMap("SELECT TOP(1) wr.work_result_id FROM order_work_results wr JOIN dispatch_assignments da ON da.dispatch_assignment_id=wr.dispatch_assignment_id AND da.state=0 WHERE da.order_id=? AND wr.state=0 AND wr.work_date=COALESCE(?,?)",Arrays.asList(id,order.get("visitDate"),order.get("lastWorkDate")));
        order.put("hasWorkResult",!reports.isEmpty());
        return order;
    }
    @PostMapping("/{id}")
    @Transactional
    public SimpleResponse<Void> save(Authentication auth,@PathVariable long id,@RequestBody Request req) {
        if(req==null)throw new BusinessException("処理を選択してください。");
        var order=guard.require(auth,id,true);
        if(req.version()==null || req.requestId()==null)throw new BusinessException("受注を開き直してください。");
        String editor=context.getAccount(auth);
        if(editor==null || editor.isBlank())throw new BusinessException("ログイン情報を確認してください。");
        String reason=req.reason()==null?"":req.reason().trim();
        var cancelReason=EnumUtil.byName(OrderCancelReason.class,req.cancelReason());
        if("CANCELLED".equals(req.type()) && reason.isBlank() && cancelReason!=null && cancelReason!=OrderCancelReason.OTHER)
            reason=cancelReason.getLabel();
        var result=EnumUtil.byName(WorkResultType.class,req.type());
        var assignments=req.workDate()==null?List.<Map<String,Object>>of():sql.selectMap("""
            SELECT TOP(1) da.dispatch_assignment_id,da.version AS assignment_version,wr.version AS result_version
            FROM dbo.dispatch_assignments da
            JOIN dbo.daily_crews dc ON dc.daily_crew_id=da.daily_crew_id AND dc.state=0
            LEFT JOIN dbo.order_work_results wr ON wr.dispatch_assignment_id=da.dispatch_assignment_id AND wr.work_date=dc.work_date AND wr.state=0
            WHERE da.order_id=? AND da.state=0 AND dc.work_date=? ORDER BY da.dispatch_assignment_id
            """,List.of(id,java.sql.Date.valueOf(req.workDate())));
        if(!"CANCELLED".equals(req.type()) && !assignments.isEmpty()) {
            var a=assignments.get(0);
            results.saveOffice(auth,id,((Number)a.get("dispatchAssignmentId")).longValue(),
                new WorkResultService.Request((Integer)a.get("assignmentVersion"),(Integer)a.get("resultVersion"),req.type(),reason,req.workDate(),req.requestId()),req.version());
        } else {
            if((result==null || !result.isConfirmationOnly()) || (order.get("outcomeType")!=null && !"ABSENT".equals(order.get("outcomeType")))) {
                if(reason.isBlank())throw new BusinessException("理由・備考を入力してください。");
            }
            outcomes.record(id,req.version(),req.type(),reason,req.cancelReason(),req.workDate(),req.requestId(),editor);
        }
        return SimpleResponse.ok("結果を保存しました。",null);
    }
}
