package com.kyouseipro.neo.domain.business.api.order.query;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.query.business.order.OrderItemFormQuery;
import com.kyouseipro.neo.sql.query.business.order.OrderItemQuery;
import com.kyouseipro.neo.sql.query.business.order.OrderQuery;
import com.kyouseipro.neo.sql.query.business.order.OrderWorkFormQuery;
import com.kyouseipro.neo.sql.query.business.order.OrderWorkQuery;

@Component
public class OrderSqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {
        Map<QueryId, QueryDefinition> map = new HashMap<>();

        // ===== Order =====
        map.put(QueryId.ORDER_DETAIL, OrderQuery.orderDetail());
        // map.put(QueryId.ORDER_LIST, OrderQuery.orderList());
        map.put(QueryId.ORDER_CSV, OrderQuery.orderCsv());
        map.put(QueryId.ORDER_DELETE_BY_IDS,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.ORDER_BY_IDS));
        map.put(QueryId.ORDER_SAVE,
            new QueryDefinition(QueryType.UPDATE, QueryKind.ORDER_SAVE, Tables.ORDER_BY_IDS));
        map.put(QueryId.ORDER_ITEM_FORM_LIST, OrderItemFormQuery.orderItemFormList());
        map.put(QueryId.ORDER_WORK_FORM_LIST, OrderWorkFormQuery.orderWorkFormList());

        // ===== OrderItem =====
        map.put(QueryId.ORDER_ITEM_DETAIL, OrderItemQuery.orderItemDetail());
        // map.put(QueryId.ORDER_ITEM_LIST, OrderItemQuery.orderItemList());
        // map.put(QueryId.ORDER_ITEM_LIST_BY_ITEM_MODEL, OrderItemQuery.orderItemListByItemModel());
        map.put(QueryId.ORDER_ITEM_CSV, OrderItemQuery.orderItemCsv());
        map.put(QueryId.ORDER_ITEM_DELETE_BY_IDS,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.ORDER_ITEM_BY_IDS));
        map.put(QueryId.ORDER_ITEM_SAVE,
            new QueryDefinition(QueryType.UPDATE, QueryKind.ORDER_SAVE, Tables.ORDER_ITEM_BY_IDS));
        map.put(QueryId.ORDER_ITEM_ARRIVAL, OrderItemQuery.orderItemArrival());
        map.put(QueryId.ORDER_ITEM_CREATE, new QueryDefinition(QueryType.UPDATE, QueryKind.ORDER_ITEM_CREATE, Tables.ORDER_ITEM_BY_IDS));
    
        // ===== OrderWork =====
        map.put(QueryId.ORDER_WORK_DETAIL, OrderWorkQuery.orderWorkDetail());
        map.put(QueryId.ORDER_WORK_LIST, OrderWorkQuery.orderWorkList());
        map.put(QueryId.ORDER_WORK_CSV, OrderWorkQuery.orderWorkCsv());
        map.put(QueryId.ORDER_WORK_DELETE_BY_IDS,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.ORDER_WORK_BY_IDS));
        map.put(QueryId.ORDER_WORK_SAVE,
            new QueryDefinition(QueryType.UPDATE, QueryKind.ORDER_SAVE, Tables.ORDER_WORK_BY_IDS));

        return map;
    }
}