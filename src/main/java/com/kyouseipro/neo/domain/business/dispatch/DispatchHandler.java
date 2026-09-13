package com.kyouseipro.neo.domain.business.dispatch;

import java.util.Map;
import org.springframework.stereotype.Component;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.*;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DispatchHandler implements QueryHandler {
    private final DispatchService service;
    public boolean supports(QueryKind kind) { return kind == QueryKind.DISPATCH; }
    public Object execute(QueryDefinition def, SelectRequest req) {
        return switch (req.getQueryId()) {
            case "dispatchList" -> Map.of("data", service.list(req.getParams()));
            case "dispatchEmployeeList" -> Map.of("data", service.employees());
            case "dispatchDetail" -> Map.of("data", service.detail(req.getParams()));
            case "dispatchSave" -> { service.save(req.getParams()); yield Map.of("count", 1); }
            default -> throw new IllegalArgumentException("Unknown dispatch query");
        };
    }
}
