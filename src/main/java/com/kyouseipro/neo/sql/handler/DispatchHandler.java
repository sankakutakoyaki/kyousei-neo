package com.kyouseipro.neo.sql.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.query.operations.dispatch.DailyCrewQuery;
import com.kyouseipro.neo.sql.service.QueryExecutor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DispatchHandler implements QueryHandler {

    private final BaseSqlRepository baseRepository; 
    private final QueryExecutor queryExecutor;

    @Override
    public boolean supports(QueryKind kind) {
        return kind == QueryKind.DAILY_CREW_BULK_CREATE;
    }

    @Override
    public Object execute(QueryDefinition def, SelectRequest req) {
        return switch (def.getKind()) {
            case DAILY_CREW_BULK_CREATE -> executeBulkCreate(req);
            default -> throw new IllegalStateException();
        };
    }

    @SuppressWarnings("unchecked")
    private Object executeBulkCreate(SelectRequest req) {

        Map<String, Object> params = req.getParams();

        String editor =
            (String) params.getOrDefault("editor", "system");

        String workDate =
            String.valueOf(params.get("workDate"));

        int officeId =
            ((Number) params.get("officeId")).intValue();

        int dispatchCategory =
            ((Number) params.get("dispatchCategory")).intValue();

        List<Number> vehicleIds =
            (List<Number>) params.get("vehicleIds");

        int count = 0;
        int skipped = 0;
        int displayOrder = 1;

        for (Number vehicleId : vehicleIds) {

            Map<String, Object> checkParams = new HashMap<>();

            checkParams.put("workDate", workDate);
            checkParams.put("officeId", officeId);
            checkParams.put("dispatchCategory", dispatchCategory);
            checkParams.put("vehicleId", vehicleId.intValue());
            checkParams.put("state", 0);

            SelectRequest checkReq = new SelectRequest();
            checkReq.setParams(checkParams);

            List<Map<String, Object>> exists =
                queryExecutor.select(
                    DailyCrewQuery.dailyCrewExists(),
                    checkReq
                );

            if (!exists.isEmpty()) {
                skipped++;
                continue;
            }

            Map<String, Object> row = new HashMap<>();

            row.put("workDate", workDate);
            row.put("officeId", officeId);
            row.put("dispatchCategory", dispatchCategory);
            row.put("vehicleId", vehicleId.intValue());
            row.put("displayOrder", displayOrder++);
            row.put("state", 0);

            baseRepository.insert(
                Tables.DAILY_CREW_BY_IDS,
                row,
                editor
            );

            count++;
        }

        return Map.of(
            "count", count,
            "skipped", skipped
        );
    }
}