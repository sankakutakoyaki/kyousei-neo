package com.kyouseipro.neo.sql.handler;

import java.util.Map;
import org.springframework.stereotype.Component;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.domain.business.order.arrival.OrderItemArrivalService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderItemArrivalHandler implements QueryHandler {
    private final OrderItemArrivalService service;
    public boolean supports(QueryKind kind) { return kind == QueryKind.ORDER_ITEM_ARRIVAL || kind == QueryKind.ORDER_ITEM_SAVE; }
    public Object execute(QueryDefinition def, SelectRequest req) {
        if (def.getKind() == QueryKind.ORDER_ITEM_SAVE) return Map.of("data", service.save(req.getParams()), "count", 1);
        if (req.getQueryId().equals("orderItemArrivalDetail"))
            return Map.of("data", service.detail(Long.parseLong(req.getParams().get("orderItemId").toString())));
        service.record(req.getParams(), req.getQueryId());
        return Map.of("count", 1);
    }
}
