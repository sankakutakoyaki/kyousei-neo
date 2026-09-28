package com.kyouseipro.neo.sql.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.query.operations.dispatch.DispatchAssignmentQuery;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.service.QueryExecutor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DispathAssignmentHandler implements QueryHandler {
    private final BaseSqlRepository baseRepository;
    private final QueryExecutor queryExecutor;

    @Override
    public boolean supports(QueryKind kind) {
        return kind == QueryKind.DISPATCH_ASSIGNMENT_REORDER
            || kind == QueryKind.DISPATCH_ASSIGNMENT_RESET;
    }

    @Override
    @Transactional
    public Object execute(QueryDefinition def, SelectRequest req) {
        return switch (def.getKind()) {
            case DISPATCH_ASSIGNMENT_REORDER -> executeAssignmentReorder(req);
            case DISPATCH_ASSIGNMENT_RESET -> executeReset(req);
            default -> throw new IllegalStateException();
        };
    }

    @SuppressWarnings("unchecked")
    private Object executeAssignmentReorder(SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        List<Map<String, Object>> items = (List<Map<String, Object>>)params.get("items");
        int count = 0;
        for(Map<String, Object> item : items){
            Map<String, Object> row = new HashMap<>();
            row.put("dispatchAssignmentId", ((Number) item.get("dispatchAssignmentId")).intValue());
            row.put("visitOrder", ((Number) item.get("visitOrder")).intValue());
            row.put("version", ((Number) item.get("version"
                )).intValue());

            baseRepository.update(Tables.DISPATCH_ASSIGNMENT_BY_IDS, row, editor);
            count++;
        }

        return Map.of(
            "count",
            count
        );
    }

    private Object executeReset(SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        String workDate = String.valueOf(params.get("workDate"));
        int officeId = ((Number) params.get("officeId")).intValue();
        int dispatchCategory = ((Number) params.get("dispatchCategory")).intValue();

        // 対象の配車割当を取得
        Map<String, Object> findParams = new HashMap<>();
        findParams.put("state", 0);
        findParams.put("workDate", workDate);
        findParams.put("officeId", officeId);
        findParams.put("dispatchCategory", dispatchCategory);

        SelectRequest findReq = new SelectRequest();
        findReq.setParams(findParams);
        List<Map<String, Object>> rows =
            queryExecutor.select(DispatchAssignmentQuery.dispatchAssignmentResetList(), findReq);

        // 論理削除
        int count = 0;
        for (Map<String, Object> row : rows) {
            Map<String, Object> update = new HashMap<>();
            update.put("dispatchAssignmentId", row.get("dispatchAssignmentId"));
            update.put("version", row.get("version"));
            update.put("state", 9);
            baseRepository.update(Tables.DISPATCH_ASSIGNMENT_BY_IDS, update, editor);
            count++;
        }

        return Map.of("count", count);
    }
}