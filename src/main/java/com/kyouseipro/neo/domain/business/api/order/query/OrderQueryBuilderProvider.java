package com.kyouseipro.neo.domain.business.api.order.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.code.OrderCategory;
import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import com.kyouseipro.neo.interfaces.sql.QueryBuilder;
import com.kyouseipro.neo.interfaces.sql.QueryBuilderPart;
import com.kyouseipro.neo.sql.query.business.order.OrderQuery;

@Component
public class OrderQueryBuilderProvider implements QueryBuilderPart {

    @Override
    public Map<QueryId, QueryBuilder> provide() {
        Map<QueryId, QueryBuilder> map = new HashMap<>();

        map.put(QueryId.ORDER_LIST, req -> {
            Integer categoryInt = toInteger(req.getParams().get("category"));
            OrderCategory category = categoryInt == null ? null: EnumUtil.of(OrderCategory.class, categoryInt);
            return OrderQuery.orderList(category);
        });
        return map;
    }
    
    private Integer toInteger(Object value){
        if(value == null) return null;

        String s = value.toString();
        if(s.isBlank()) return null;

        return Integer.parseInt(s);
    }
}