package com.kyouseipro.neo.sql.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.interfaces.sql.QueryBuilder;
import com.kyouseipro.neo.sql.common.QueryParamBinder;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.QueryBuilderProvider;
import com.kyouseipro.neo.sql.provider.QueryHandlerProvider;
import com.kyouseipro.neo.sql.provider.SqlProvider;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.sql.service.CsvService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QueryController {

    private final SqlProvider sqlProvider;
    private final QueryParamBinder paramBinder;
    private final SqlRepository sqlRepository;
    private final BaseSqlRepository baseRepository;
    private final CsvService csvService;
    private final QueryBuilderProvider queryBuilderProvider;
    private final QueryHandlerProvider handlerProvider;

    @PostMapping("/query")
    public Object request(@RequestBody SelectRequest req) {
        QueryId queryId = QueryId.from(req.getQueryId());
        QueryBuilder builder = queryBuilderProvider.get(queryId);
        QueryDefinition def;

        if (builder != null) {
            def = builder.build(req);
        } else {
            def = sqlProvider.get(queryId);
        }

        return switch (def.getKind()) {
            case SQL -> executeSql(def, req);
            case SAVE -> executeSave(def, req);
            case INSERT -> executeInsert(def, req);
            case UPDATE -> executeUpdate(def, req);
            case DELETE_BY_IDS -> executeDeleteByIds(def, req);
            case CSV -> executeCsv(def, req);
            default -> handlerProvider.get(def.getKind()).execute(def, req);
        };
    }

    // SQL
    private Object executeSql(QueryDefinition def, SelectRequest req) {
        if (def.getType() == QueryType.SELECT) {
            return Map.of("data", select(def, req));
        }
        List<Object> params = paramBinder.build(def.getParamOrder(), req.getParams());
        int count = sqlRepository.update(def.getSql(), params);
        return Map.of("count", count);
    }

    private List<Map<String, Object>> select(QueryDefinition def, SelectRequest req) {
        List<Object> params = paramBinder.build(def.getParamOrder(), req.getParams());
        return sqlRepository.selectMap(def.getSql(), params);
    }

    private Object executeSave(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        Object idValue = params.get(def.getTableMeta().idColumn());

        boolean isInsert = idValue == null || Long.valueOf(idValue.toString()) == 0;
        String editor = (String) params.getOrDefault("editor", "system");

        // itemsを注文本体から除外
        Map<String, Object> orderParams = new HashMap<>(params);
        orderParams.remove("items");

        if (isInsert) {
            Long id = baseRepository.insert(
                def.getTableMeta(),
                orderParams,
                editor
            );
            return Map.of(
                "data", id,
                "count", 1
            );
        }

        int count = baseRepository.update(
            def.getTableMeta(),
            orderParams,
            editor
        );
        return Map.of(
            "data", idValue,
            "count", count
        );
    }

    // INSERT
    private Object executeInsert(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        Long id = baseRepository.insert(def.getTableMeta(), params, editor);
        return Map.of("data", id);
    }

    // UPDATE
    private Object executeUpdate(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        int count = baseRepository.update(def.getTableMeta(), params, editor);
        return Map.of("count", count);
    }

    // DELETE_BY_IDS
    private Object executeDeleteByIds(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        String editor = (String) params.getOrDefault("editor", "system");
        int count = baseRepository.deleteByIds(def.getTableMeta(), ids, editor);
        return Map.of("count", count);
    }

    // CSV
    private Object executeCsv(QueryDefinition def, SelectRequest req) {
        return csvService.execute(def, req);
    }
}