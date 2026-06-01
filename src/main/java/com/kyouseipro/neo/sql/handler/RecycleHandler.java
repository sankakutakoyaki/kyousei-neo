package com.kyouseipro.neo.sql.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.domain.business.api.recycle.RecycleRepository;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.common.QueryParamBinder;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecycleHandler implements QueryHandler {

    private final RecycleRepository recycleRepository;
    private final BaseSqlRepository baseRepository;
    private final SqlRepository sqlRepository;
    private final QueryParamBinder paramBinder;

    @Override
    public boolean supports(QueryKind kind) {
        return switch(kind) {
            case RECYCLE_DELIVERY_SAVE,
                 RECYCLE_SHIPPING_SAVE,
                 RECYCLE_LOSS_SAVE,
                 RECYCLE_PRICE_LIST,
                 RECYCLE_PRICE_DETAIL,
                 RECYCLE_PRICE_SAVE
                    -> true;

            default -> false;
        };
    }

    @Override
    public Object execute(QueryDefinition def, SelectRequest req) {
        return switch(def.getKind()) {
            case RECYCLE_DELIVERY_SAVE -> executeDelivery(def, req);
            case RECYCLE_SHIPPING_SAVE -> executeShipping(def, req);
            case RECYCLE_LOSS_SAVE -> executeLoss(def, req);
            case RECYCLE_PRICE_LIST -> executePriceList(def, req);
            case RECYCLE_PRICE_DETAIL -> executePriceDetail(def, req);
            case RECYCLE_PRICE_SAVE -> executePriceSave(def, req);

            default -> throw new IllegalStateException();
        };
    }

    // RECYCLE
    private Object executeDelivery(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        int count = recycleRepository.updateRecycleDelivery(def.getTableMeta(), params, editor);
        return Map.of("count", count);
    }
    private Object executeShipping(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        int count = recycleRepository.updateRecycleShipping(def.getTableMeta(), params, editor);
        return Map.of("count", count);
    }
    private Object executeLoss(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        int count = recycleRepository.updateRecycleLoss(def.getTableMeta(), params, editor);
        return Map.of("count", count);
    }

    private Object executePriceList(QueryDefinition def, SelectRequest req) {
        List<Map<String, Object>> records = select(def, req);
        Map<Long, Map<String, Object>> makers = new LinkedHashMap<>();

        for (Map<String, Object> record : records) {
            Long makerId = ((Number) record.get("recycleMakerId")).longValue();
            Map<String, Object> row = 
                makers.computeIfAbsent(makerId, id -> {
                    Map<String, Object> r = new LinkedHashMap<>();
                    r.put("recycleMakerId", id);
                    r.put("code", record.get("code"));
                    r.put("name", record.get("name"));
                    r.put("kana", record.get("kana"));
                    return r;
                });

            Object itemObj = record.get("recycleItemId");
            if(itemObj == null) continue;

            Long itemId = ((Number)itemObj).longValue();
            row.put("price_" + itemId, record.get("price"));
        }
        return Map.of("data", new ArrayList<>(makers.values()));
    }

    private Object executePriceDetail(QueryDefinition def, SelectRequest req) {
        List<Map<String,Object>> records = select(def, req);

        if(records.isEmpty()){
            return Map.of("data", List.of());
        }

        Map<String,Object> row = new LinkedHashMap<>();
        Map<String,Object> first = records.get(0);
        row.put("recycleMakerId", first.get("recycleMakerId"));
        row.put("name", first.get("name"));

        for(Map<String,Object> record : records){
            Object itemObj = record.get("recycleItemId");
            if(itemObj == null) continue;

            int itemId = ((Number)itemObj).intValue();
            row.put("price" + itemId, record.get("price"));
            row.put("recyclePriceId" + itemId, record.get("recyclePriceId"));
        }
        return Map.of("data", List.of(row));
    }

    @SuppressWarnings("unchecked")
    private Object executePriceSave(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        List<Map<String, Object>> details = (List<Map<String, Object>>) params.get("details");
        int count = 0;

        for (Map<String, Object> detail : details) {
            Long id = detail.get("recyclePriceId") == null ? null: ((Number) detail.get("recyclePriceId")).longValue();
            BigDecimal price = detail.get("price") == null ? BigDecimal.ZERO: new BigDecimal(detail.get("price").toString());

            if (id == null) {
                if (price.compareTo(BigDecimal.ZERO) > 0) {
                    baseRepository.insert(Tables.RECYCLE_PRICE_BY_IDS, detail, editor);
                    count++;
                }
                continue;
            }

            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                baseRepository.deleteByIds(Tables.RECYCLE_PRICE_BY_IDS, List.of(id), editor);
                count++;
                continue;
            }
            count += baseRepository.update(Tables.RECYCLE_PRICE_BY_IDS, detail, editor);
        }
        return Map.of("count", count);
    }

    private List<Map<String, Object>> select(QueryDefinition def, SelectRequest req) {
        List<Object> params = paramBinder.build(def.getParamOrder(), req.getParams());
        return sqlRepository.selectMap(def.getSql(), params);
    }
}