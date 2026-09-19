package com.kyouseipro.neo.domain.operations.dispatch.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.query.operations.dispatch.DispatchOrderQuery;

@Component
public class DispatchOrderSqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {

        Map<QueryId, QueryDefinition> map = new HashMap<>();

        map.put(
            QueryId.DISPATCH_ORDER_LIST,
            DispatchOrderQuery.dispatchOrderList()
        );

        return map;
    }
}