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
import com.kyouseipro.neo.sql.query.business.order.OrderQuery;

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
            new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.ORDER_BY_IDS));

        return map;
    }
}