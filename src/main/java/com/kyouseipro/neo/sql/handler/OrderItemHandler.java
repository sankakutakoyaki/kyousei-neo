package com.kyouseipro.neo.sql.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderItemHandler implements QueryHandler {

    private final BaseSqlRepository baseRepository;

    @Override
    public boolean supports(QueryKind kind) {
        return kind == QueryKind.ORDER_ITEM_CREATE;
    }

    @Override
    @Transactional
    public Object execute(
            QueryDefinition def,
            SelectRequest req
    ) {

        Map<String, Object> params = req.getParams();

        String editor = (String) params.getOrDefault("editor", "system");

        // =========================
        // 登録データ
        // =========================

        Map<String, Object> itemParams = new LinkedHashMap<>();

        itemParams.put("janCode", params.get("janCode"));
        itemParams.put("itemName", params.get("itemName"));
        itemParams.put("itemMaker", params.get("itemMaker"));
        itemParams.put("itemModel", params.get("itemModel"));
        itemParams.put("remarks", params.get("remarks"));

        // 受注とは紐付かない単独商品
        itemParams.put("orderId", 0);

        // 登録時点では未入荷
        itemParams.put("arrivalDate", java.sql.Date.valueOf(java.time.LocalDate.now()));

        // 有効状態
        itemParams.put(
                "state",
                State.INITIAL.getCode()
        );

        // =========================
        // INSERT
        // =========================

        Long orderItemId =
                baseRepository.insert(
                        def.getTableMeta(),
                        itemParams,
                        editor
                );

        return Map.of(
                "data", orderItemId,
                "count", 1
        );
    }
}