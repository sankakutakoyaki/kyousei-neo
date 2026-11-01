package com.kyouseipro.neo.domain.business.order.outcome;

import java.util.*;
import com.kyouseipro.neo.common.enums.code.*;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.repository.*;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.domain.business.order.history.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderOutcomeService {
    private final SqlRepository sql;
    private final BaseSqlRepository base;
    private final OrderHistoryRepository histories;

    @Transactional
    public void record(long id, Integer expectedVersion, String type, String reason, String cancelReason,
                       LocalDate day, UUID requestId, String editor) {
        var rows=sql.selectMap("SELECT * FROM dbo.orders WITH (UPDLOCK,HOLDLOCK) WHERE order_id=? AND state=0",List.of(id));
        if(rows.isEmpty())throw new BusinessException("受注が見つかりません。");
        var current=rows.get(0);
        var duplicate=sql.selectMap("SELECT TOP(1) outcome_type,outcome_reason,cancel_reason,last_work_date FROM dbo.orders_log WHERE order_id=? AND outcome_request_id=? ORDER BY version",List.of(id,requestId.toString()));
        if(!duplicate.isEmpty()) {
            var old=duplicate.get(0);
            if(!Objects.equals(old.get("outcomeType"),type) || !Objects.equals(old.get("outcomeReason"),reason)
                || !Objects.equals(old.get("cancelReason"),cancelReason) || !Objects.equals(Objects.toString(old.get("lastWorkDate"),null),Objects.toString(day,null)))
                throw new BusinessException("登録要求が重複しています。");
            return;
        }
        if(expectedVersion!=null && !expectedVersion.equals(current.get("version")))throw new BusinessException("受注が更新されています。開き直してください。");
        if("CANCELLED".equals(current.get("orderStatus")))throw new BusinessException("キャンセル済みの受注です。");
        var result=EnumUtil.byName(WorkResultType.class,type);
        if(!OrderStatus.CANCELLED.name().equals(type) && (result==null || !result.isReportable()))throw new BusinessException("結果を選択してください。");
        if(reason==null || reason.length()>1000 || ((result==null || !result.isConfirmationOnly()) && reason.isBlank()))throw new BusinessException("理由・備考を1000文字以内で入力してください。");
        if("CANCELLED".equals(type) && EnumUtil.byName(OrderCancelReason.class,cancelReason)==null)throw new BusinessException("キャンセル理由を選択してください。");
        String planned=Objects.toString(current.get("visitDate"),null);
        String last=Objects.toString(current.get("lastWorkDate"),null);
        if(!"CANCELLED".equals(type) && (day==null || (planned!=null && !planned.equals(day.toString())) || (planned==null && last!=null && !last.equals(day.toString()))))
            throw new BusinessException("訪問予定が変更されています。開き直してください。");
        Map<String,Object> data=new LinkedHashMap<>();
        data.put("orderId",id);data.put("version",current.get("version"));
        data.put("orderStatus",(result==null?OrderStatus.CANCELLED:result.getOrderStatus()).name());
        data.put("outcomeType",type);data.put("outcomeReason",reason);data.put("cancelReason",cancelReason);
        data.put("lastWorkDate",day==null?null:java.sql.Date.valueOf(day));data.put("outcomeRequestId",requestId.toString());
        if(Set.of("DELIVERY_FAILED","POSTPONED").contains(type)){data.put("visitDate",null);data.put("visitTime",null);}
        data.put("outcomeReportedAt",sql.selectMap("SELECT SYSDATETIME() AS reported_at",List.of()).get(0).get("reportedAt"));
        base.update(Tables.ORDER_BY_IDS,data,editor);
        histories.recordSaved(id,false,editor);
    }
}
