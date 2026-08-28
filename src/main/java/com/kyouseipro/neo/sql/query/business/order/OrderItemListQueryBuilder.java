package com.kyouseipro.neo.sql.query.business.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.sql.QueryBuilder;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;

@Component
public class OrderItemListQueryBuilder implements QueryBuilder {

    @Override
    public QueryDefinition build(SelectRequest req) {

        Map<String, Object> params = req.getParams();

        StringBuilder sql = new StringBuilder("""
            SELECT
                oi.order_item_id,
                oi.order_id,
                oi.arrival_date,
                oi.jan_code,
                oi.item_maker,
                oi.item_name,
                oi.item_model,
                oi.item_quantity,
                oi.item_payment,
                oi.remarks,
                oi.version,
                oi.state,
                o.request_number,
                o.prime_constractor_id,
                o.prime_constractor_office_id,
                c.name as prime_constractor_name,
                cf.name as prime_constractor_office_name

            FROM order_items oi
            LEFT OUTER JOIN orders o ON o.order_id = oi.order_id AND o.state = ?
            LEFT OUTER JOIN companies c ON c.company_id = o.prime_constractor_id AND c.state = ?
            LEFT OUTER JOIN offices cf ON cf.office_id = o.prime_constractor_office_id AND cf.state = ?
            WHERE oi.state = ?
            """);

        List<String> paramOrder = new ArrayList<>();
        paramOrder.add("state");
        paramOrder.add("state");
        paramOrder.add("state");
        paramOrder.add("state");

        // addArrivalCondition(sql, params, "category");
        addLikeCondition(sql, paramOrder, params, "janCode", "oi.jan_code");
        addLikeCondition(sql, paramOrder, params, "itemMaker", "oi.item_maker");
        addLikeCondition(sql, paramOrder, params, "itemName", "oi.item_name");
        addLikeCondition(sql, paramOrder, params, "itemModel", "oi.item_model");

        sql.append(" ORDER BY oi.order_item_id");

        return QueryDefinition.select(
            sql.toString(),
            paramOrder
        );
    }

    private void addLikeCondition(
            StringBuilder sql,
            List<String> paramOrder,
            Map<String, Object> params,
            String paramName,
            String column
    ) {
        Object value = params.get(paramName);

        if (value == null) {
            return;
        }

        String text = String.valueOf(value).trim();

        if (text.isEmpty()) {
            return;
        }

        sql.append(" AND ")
           .append(column)
           .append(" LIKE '%' + ? + '%'");

        paramOrder.add(paramName);
    }
}