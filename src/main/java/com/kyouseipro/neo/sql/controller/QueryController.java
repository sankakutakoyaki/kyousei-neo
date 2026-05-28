package com.kyouseipro.neo.sql.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kyouseipro.neo.common.enums.code.State;
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
            case RECYCLE_DELIVERY_SAVE,
                 RECYCLE_SHIPPING_SAVE,
                 RECYCLE_LOSS_SAVE
                -> executeRecycle(def, req);
            // case RECYCLE_MAKER_SAVE -> executeRecycleMaker(def, req);
            case RECYCLE_PRICE_SAVE -> executeRecyclePrices(def, req);
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
            return Map.of("data", id, "count", 1);
        }
        int count = baseRepository.update(def.getTableMeta(), params, editor);
        return Map.of("data", idValue, "count", count);
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

    // // RECYCLE_MAKER
    // @Transactional
    // private Object executeRecycleMaker(QueryDefinition def, SelectRequest req){
    //     SelectRequest maker = createRecycleMakerEntity(req);
    //     executeSave(def, maker);
    //     Long id = Long.parseLong(maker.getParams().get("recycleMakerId").toString());
    //     return Map.of("data", id);
    // }

    @Transactional
    private Object executeRecyclePrices(QueryDefinition def, SelectRequest req){
        Map<String, Object> params = req.getParams();

        List<Map<String, Object>> prices = (List<Map<String, Object>>) params.get("prices");
        Long makerId = (Long) params.get("recycleMakerId");
        for(Map<String, Object> item : prices){
            SelectRequest priceReq = createRecyclePriceEntity(makerId, item, req);
            executeSave(def, priceReq);
        }
        return Map.of("count", 1);
    }

    // private SelectRequest createRecycleMakerEntity(SelectRequest req){
    //     Map<String, Object> params = req.getParams();
    //     String editor = (String) params.getOrDefault("editor", "system");

    //     Map<String,Object> insert = new HashMap<>();
    //     putIfPresent(insert, "code", params);
    //     putIfPresent(insert, "name", params);
    //     putIfPresent(insert, "kana", params);
    //     putIfPresent(insert, "abbrName", params);
    //     putIfPresent(insert, "group", params);
    //     insert.put("state", State.INITIAL.getCode());
    //     insert.put("editor", editor);

    //     SelectRequest request = new SelectRequest();
    //     request.setQueryId(req.getQueryId());
    //     request.setParams(insert);
    //     return request;
    // }

    private SelectRequest createRecyclePriceEntity(Long id, Map<String, Object> item, SelectRequest req){
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");

        Map<String,Object> insert = new HashMap<>();
        insert.put("recycleMakerId", id);
        insert.put("recycleItemId", item.get("recycleItemId"));
        insert.put("price", item.get("price"));
        insert.put("taxPrice", item.get("taxPrice"));
        insert.put("state", State.INITIAL.getCode());
        insert.put("editor", editor);

        SelectRequest request = new SelectRequest();
        request.setQueryId("recyclePriceSave");
        request.setParams(insert);
        return request;
    }

    private void putIfPresent(
        Map<String, Object> target,
        String key,
        Map<String, Object> source
    ){
        Object value = source.get(key);
        if(value == null) return;

        if(value instanceof String str && str.isBlank()){
            return;
        }

        target.put(key, value);
    }
}