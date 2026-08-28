package com.kyouseipro.neo.sql.query.business.order;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.interfaces.sql.QueryBuilder;
import com.kyouseipro.neo.interfaces.sql.QueryBuilderPart;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderItemQueryBuilderPart implements QueryBuilderPart {

    private final OrderItemListQueryBuilder orderItemListQueryBuilder;

    @Override
    public Map<QueryId, QueryBuilder> provide() {

        Map<QueryId, QueryBuilder> map = new LinkedHashMap<>();

        map.put(
            QueryId.ORDER_ITEM_LIST,
            orderItemListQueryBuilder
        );

        return map;
    }
}