package com.kyouseipro.neo.sql.query.business.order;

import java.util.List;

import com.kyouseipro.neo.common.enums.code.OrderCategory;
import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class OrderQuery {
    public static QueryDefinition orderList(OrderCategory category) {
        String column = category.getColumn();
        String sql = """
            SELECT o.order_id, o.request_number, o.visit_date, o.visit_time, o.title, o.full_address, o.remarks,
                c.name as prime_constractor_name, co.name as prime_constractor_office_name, s.full_name as staff_name,
                o.version, o.state
            FROM orders o
            LEFT OUTER JOIN companies c ON c.company_id = o.prime_constractor_id AND c.state = ?
            LEFT OUTER JOIN offices co ON co.office_id = o.prime_constractor_office_id AND co.state = ?
            LEFT OUTER JOIN staffs s ON s.staff_id = o.staff_id AND s.state = ?
            WHERE (o.state = ? OR o.state = ?) AND o.%s >= ? AND o.%s < ?
            """.formatted(column, column);

        return QueryDefinition.select(
            sql,
            List.of("state", "state", "state", "state", "compState", "dateFrom", "dateTo")
        );
    }

    public static QueryDefinition orderDetail() {
        return QueryDefinition.select(
            """
            SELECT o.order_id, o.request_number, o.visit_date, o.visit_time, o.title, o.postal_code, o.full_address, o.contact_information, o.remarks,
                o.prime_constractor_id, o.prime_constractor_office_id, o.staff_id,
                o.version, o.state
            FROM orders o
            WHERE o.state = ? AND order_id = ?;
            """,
            List.of("state", "orderId")
        );
    }

    public static QueryDefinition orderCsv() {
        String sql = """
            SELECT
                o.request_number,
                c.name as company_name,
                o.name as office_name,
                o.visit_date,
                o.visit_time,
                o.full_address,
                o.title
            FROM orders o
            LEFT OUTER JOIN companies c ON c.company_id = r.company_id AND c.state = ?
            LEFT OUTER JOIN offices o ON o.office_id = r.office_id AND o.state = ?
            WHERE o.state = ?
            AND o.order_id IN (:ids)
        """;

        List<String> params = List.of("state", "state", "state", "ids");

        List<CsvColumn> columns = List.of(
            new CsvColumn("requestNumber", "発注番号"),
            new CsvColumn("companyName", "荷主"),
            new CsvColumn("officeName", "支店"),
            new CsvColumn("visitDate", "訪問日"),
            new CsvColumn("visitTime", "訪問時間"),
            new CsvColumn("fullAddress", "住所"),
            new CsvColumn("title", "件名")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}
