package com.kyouseipro.neo.sql.query.business.order;

import java.util.List;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class OrderItemFormQuery {
    public static QueryDefinition orderItemFormList() {
        return QueryDefinition.select(
            """
            SELECT o.order_item_id, o.order_id, o.arrival_date, o.jan_code,
                o.item_maker, o.item_name, o.item_model, o.item_quantity, o.item_payment, o.remarks,
                o.version, o.state
            FROM order_items o
            WHERE o.state = ? AND order_id = ?;
            """,
            List.of("state", "orderId")
        );
    }

    public static QueryDefinition orderItemFormDetail() {
        return QueryDefinition.select(
            """
            SELECT o.order_item_id, o.order_id, o.arrival_date, o.jan_code,
                o.item_maker, o.item_name, o.item_model, o.item_quantity, o.item_payment, o.remarks,
                o.version, o.state
            FROM order_items o
            WHERE o.state = ? AND order_item_id = ?;
            """,
            List.of("state", "orderItemId")
        );
    }

    public static QueryDefinition orderItemFormCsv() {
        String sql = """
            SELECT
                o.order_item_id,
                o.arrival_date,
                o.jan_code,
                o.item_maker,
                o.item_name,
                o.item_model,
                o.item_quantity,
                o.item_payment,
                o.remarks
            FROM order_items o
            WHERE o.state = ?
            AND o.order_id IN (:ids)
        """;

        List<String> params = List.of("state", "ids");

        List<CsvColumn> columns = List.of(
            new CsvColumn("orderItemId", "ID"),
            new CsvColumn("arrivalDate", "入荷日"),
            new CsvColumn("janCode", "JANコード"),
            new CsvColumn("itemMaker", "メーカー"),
            new CsvColumn("itemName", "商品名"),
            new CsvColumn("itemModel", "型番"),
            new CsvColumn("itemQuantity", "数量"),
            new CsvColumn("itemPayment", "金額"),
            new CsvColumn("remarks", "備考")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}
