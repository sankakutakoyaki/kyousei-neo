package com.kyouseipro.neo.sql.query.business.order;

import java.util.List;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class OrderWorkQuery {

    public static QueryDefinition orderWorkList() {
        return QueryDefinition.select(
            """
            SELECT
                o.order_work_id,
                o.order_id,
                o.order_work_code,
                o.order_work_name,
                o.order_work_price,
                o.order_work_quantity,
                o.remarks,
                o.version,
                o.state
            FROM order_works o
            WHERE o.state = ?
              AND o.order_id = ?
            """,
            List.of("state", "orderId")
        );
    }
    public static QueryDefinition orderWorkDetail() {
        return QueryDefinition.select(
            """
            SELECT
                o.order_work_id,
                o.order_id,
                o.order_work_code,
                o.order_work_name,
                o.order_work_price,
                o.order_work_quantity,
                o.remarks,
                o.version,
                o.state
            FROM order_works o
            WHERE o.state = ? AND order_work_id = ?;
            """,
            List.of("state", "orderWorkId")
        );
    }

    public static QueryDefinition orderWorkCsv() {
        String sql = """
            SELECT
                o.order_work_id,
                o.order_id,
                o.order_work_code,
                o.order_work_name,
                o.order_work_price,
                o.order_work_quantity,
                o.remarks,
                o.version,
                o.state
            FROM order_works o
            WHERE o.state = ?
            AND o.order_id IN (:ids)
        """;

        List<String> params = List.of("state", "ids");

        List<CsvColumn> columns = List.of(
            new CsvColumn("orderWorkId", "ID"),
            new CsvColumn("orderId", "受注番号"),
            new CsvColumn("orderWorkCode", "作業コード"),
            new CsvColumn("orderWorkName", "作業内容"),
            new CsvColumn("orderWorkPrice", "金額"),
            new CsvColumn("orderWorkQuantity", "数量"),
            new CsvColumn("remarks", "備考")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}
