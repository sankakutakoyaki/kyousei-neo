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
import com.kyouseipro.neo.domain.business.api.recycle.RecycleRepository;
import com.kyouseipro.neo.interfaces.sql.QueryBuilder;
import com.kyouseipro.neo.sql.common.QueryParamBinder;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.QueryBuilderProvider;
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
    private final RecycleRepository recycleRepository;

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
            // case RECYCLE_DELIVERY_SAVE -> executeRecycleDelivery(def, req);
            case RECYCLE_DELIVERY_SAVE,
                 RECYCLE_SHIPPING_SAVE,
                 RECYCLE_LOSS_SAVE
                -> executeRecycle(def, req);
        };
    }

    // SQL
    private Object executeSql(QueryDefinition def, SelectRequest req) {
        List<Object> params = paramBinder.build(def.getParamOrder(), req.getParams());
        if (def.getType() == QueryType.SELECT) {
            var result = sqlRepository.selectMap(def.getSql(), params);
            return Map.of("data", result);
        }
        int count = sqlRepository.update(def.getSql(), params);
        return Map.of("count", count);
    }

    // SAVE
    private Object executeSave(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor","system");
        Object idsObj = params.get("ids");
        // 一括更新
        if (idsObj != null) {
            @SuppressWarnings("unchecked")
            List<Object> ids = (List<Object>) idsObj;
            if (ids.isEmpty()) {
                return Map.of("count", 0);
            }
            Map<String, Object> diff = new HashMap<>(params);
            diff.remove("ids");
            diff.remove(def.getTableMeta().idColumn());
            diff.remove(def.getTableMeta().versionColumn());

            if (diff.isEmpty()) {
                return Map.of("count", 0);
            }

            int count = baseRepository.updateByIds(def.getTableMeta(), ids, diff, editor);
            return Map.of("count", count);
        }
        // 単一
        Object idValue = params.get(def.getTableMeta().idColumn());
        boolean isInsert = (idValue == null) || (Long.valueOf(idValue.toString()) == 0);
        if (isInsert) {
            Long id = baseRepository.insert(def.getTableMeta(), params, editor);
            return Map.of("data", id);
        }
        int count = baseRepository.update(def.getTableMeta(), params, editor);
        return Map.of("count", count);
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

    // RECYCLE
    private Object executeRecycle(QueryDefinition def, SelectRequest req){
        return switch(def.getKind()) {
            case RECYCLE_DELIVERY_SAVE -> executeRecycleDelivery(def, req);
            case RECYCLE_SHIPPING_SAVE -> executeRecycleShipping(def, req);
            case RECYCLE_LOSS_SAVE -> executeRecycleLoss(def, req);
            default ->
                throw new IllegalStateException("未対応 recycle kind: " + def.getKind());
        };
    }

    // 
    // RECYCLE
    private Object executeRecycleDelivery(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        int count = recycleRepository.updateRecycleDelivery(def.getTableMeta(), params, editor);
        return Map.of("count", count);
    }
    private Object executeRecycleShipping(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        int count = recycleRepository.updateRecycleShipping(def.getTableMeta(), params, editor);
        return Map.of("count", count);
    }
    private Object executeRecycleLoss(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        int count = recycleRepository.updateRecycleLoss(def.getTableMeta(), params, editor);
        return Map.of("count", count);
    }
}

// @RestController
// @RequestMapping("/api")
// @RequiredArgsConstructor
// public class QueryController {
//     private final SqlProvider sqlProvider;
//     private final QueryParamBinder paramBinder;
//     private final SqlRepository sqlRepository;
//     private final BaseSqlRepository baseRepository;
//     private final CsvService csvService;
//     private final QueryBuilderProvider queryBuilderProvider;
//     private final RecycleRepository recycleRepository;

//     @PostMapping("/query")
//     public Object request(@RequestBody SelectRequest req) {

//         // QueryDefinition def = sqlProvider.get(req.getQueryId());
//         QueryId queryId = QueryId.from(req.getQueryId());
//         QueryBuilder builder = queryBuilderProvider.get(queryId);
//         QueryDefinition def;
//         if (builder != null) {
//             def = builder.build(req);
//         } else {
//             def = sqlProvider.get(queryId);
//         }

//         switch (def.getKind()) {

//             // ========================
//             // SQL（SELECT / UPDATEなど）
//             // ========================
//             case SQL -> {
//                 List<Object> params =
//                     paramBinder.build(def.getParamOrder(), req.getParams());

//                 if (def.getType() == QueryType.SELECT) {
//                     var result = sqlRepository.selectMap(def.getSql(), params);
//                     return Map.of(
//                         "data", result
//                     );
//                 } else {
//                     int count = sqlRepository.update(def.getSql(), params);
//                     return Map.of(
//                         "count", count
//                     );
//                 }
//             }

//             // ========================
//             // SAVE
//             // ========================
//             case SAVE -> {
//                 Map<String, Object> params = req.getParams();
//                 String editor = (String) params.getOrDefault("editor", "system");
//                 Object idsObj = params.get("ids");
//                 if (idsObj != null) {
//                     @SuppressWarnings("unchecked")
//                     List<Object> ids = (List<Object>) idsObj;
//                     if (ids.isEmpty()) {
//                         return Map.of("count", 0);
//                     }

//                     Map<String, Object> diff = new HashMap<>(params);
//                     diff.remove("ids");
//                     diff.remove(def.getTableMeta().idColumn());
//                     diff.remove(def.getTableMeta().versionColumn());
//                     if (diff.isEmpty()) {
//                         return Map.of("count", 0);
//                     }
//                     int count = baseRepository.updateByIds(
//                         def.getTableMeta(),
//                         ids,
//                         diff,
//                         editor
//                     );
//                     return Map.of("count", count);
//                 }

//                 // 単一処理
//                 Object idValue = params.get(def.getTableMeta().idColumn());
//                 boolean isInsert = (idValue == null) || (Long.valueOf(idValue.toString()) == 0);
//                 if (isInsert) {
//                     Long id = baseRepository.insert(
//                         def.getTableMeta(),
//                         params,
//                         editor
//                     );
//                     return Map.of("data", id);
//                 } else {
//                     int count = baseRepository.update(
//                         def.getTableMeta(),
//                         params,
//                         editor
//                     );
//                     return Map.of("count", count);
//                 }
//             }

//             // ========================
//             // INSERT
//             // ========================
//             case INSERT -> {

//                 Map<String, Object> params = req.getParams();

//                 String editor = (String) params.getOrDefault("editor", "system");

//                 Long id = baseRepository.insert(
//                     def.getTableMeta(),
//                     params,
//                     editor
//                 );

//                 return Map.of(
//                     "data", id
//                 );
//             }

//             // ========================
//             // UPDATE
//             // ========================
//             case UPDATE -> {

//                 Map<String, Object> params = req.getParams();

//                 String editor = (String) params.getOrDefault("editor", "system");

//                 int count = baseRepository.update(
//                     def.getTableMeta(),
//                     params,
//                     editor
//                 );

//                 return Map.of(
//                     "count", count
//                 );
//             }

//             // ========================
//             // DELETE_BY_IDS
//             // ========================
//             case DELETE_BY_IDS -> {
//                 Map<String, Object> params = req.getParams();
//                 List<Long> ids = (List<Long>) params.get("ids");
//                 String editor = (String) params.getOrDefault("editor", "system");
//                 int count = baseRepository.deleteByIds(
//                     def.getTableMeta(), ids, editor
//                 );
//                 return Map.of("count", count);
//             }

//             // ========================
//             // CSV_DOWNLOAD
//             // ========================
//             case CSV -> {
//                 return csvService.execute(def, req);
//             }

//             // ========================
//             // RECYCLE_DELIVERY
//             // ========================
//             case RECYCLE_DELIVERY_SAVE -> {
//                 Map<String, Object> params = req.getParams();
//                 String editor = (String) params.getOrDefault("editor","system");
//                 int count = recycleRepository.updateRecycleDelivery(
//                     def.getTableMeta(), params, editor
//                 );
//                 return Map.of("count", count);
//             }
//         }

//         throw new IllegalStateException("未対応のQueryKind: " + def.getKind());
//     }
// }
