package com.kyouseipro.neo.domain.operations;

import java.util.*;
import org.springframework.stereotype.Component;
import com.kyouseipro.neo.common.enums.system.*;
import com.kyouseipro.neo.interfaces.sql.*;
import com.kyouseipro.neo.sql.model.*;
import lombok.RequiredArgsConstructor;

@Component @RequiredArgsConstructor
public class OperationQueries implements SqlProviderPart, QueryHandler {
    private final OperationService service;
    private final OperationScheduleService schedules;
    public Map<QueryId,QueryDefinition> provide() {
        var result=new HashMap<QueryId,QueryDefinition>();
        for(QueryId id:QueryId.values()) if(id.name().startsWith("OPERATION_")) result.put(id,new QueryDefinition(id.name().endsWith("_SAVE")?QueryType.UPDATE:QueryType.SELECT,QueryKind.OPERATIONS,null));
        return result;
    }
    public boolean supports(QueryKind kind) { return kind==QueryKind.OPERATIONS; }
    public Object execute(QueryDefinition def,SelectRequest req) {
        var p=req.getParams();
        return Map.of("data",switch(req.getQueryId()) {
            case "operationEmployeeQualificationsList" -> service.employeeQualifications(p);
            case "operationList" -> service.list(p);
            case "operationDetail" -> service.detail(p);
            case "operationCodeDetail" -> service.lookup(p);
            case "operationSave" -> service.save(p);
            case "operationDispatchList" -> schedules.dispatchList(p);
            case "operationDispatchDetail" -> schedules.dispatchDetail(p);
            case "operationDispatchSave" -> schedules.dispatchSave(p);
            case "operationCrewDetail" -> schedules.crewLookup(p);
            default -> throw new IllegalArgumentException("Unknown operation query");
        });
    }
}
