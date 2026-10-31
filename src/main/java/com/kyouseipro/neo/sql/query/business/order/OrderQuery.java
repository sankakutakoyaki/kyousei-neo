package com.kyouseipro.neo.sql.query.business.order;

import java.util.List;

import com.kyouseipro.neo.common.enums.code.OrderCategory;
import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class OrderQuery {
    private static final String ACCESS_CONDITION = """
        AND (
            ? = 'ALL'
            OR (
                ? IN ('OWN', 'OWN_ASSIGNED')
                AND o.own_office_id = ?
            )
            OR (
                ? IN ('ASSIGNED', 'OWN_ASSIGNED')
                AND EXISTS (
                    SELECT 1
                    FROM dispatch_assignments da
                    JOIN daily_crews dc
                        ON dc.daily_crew_id = da.daily_crew_id
                    JOIN daily_crew_members dcm
                        ON dcm.daily_crew_id = dc.daily_crew_id
                    WHERE da.order_id = o.order_id
                    AND dcm.employee_id = ?
                    AND da.state = 0
                    AND dc.state = 0
                    AND dcm.state = 0
                )
            )
        )
        """;
    private static final List<String> ACCESS_PARAMS = List.of(
        "accessScope",
        "accessScope",
        "accessOfficeId",
        "accessScope",
        "accessEmployeeId"
    );

    public static QueryDefinition orderList(OrderCategory category) {
        String column = category.getColumn();

        String sql = """
            SELECT
                o.order_id,
                o.request_number,
                o.visit_date,
                o.visit_time,
                o.title,
                o.full_address,
                o.remarks,
                c.name AS prime_constractor_name,
                co.name AS prime_constractor_office_name,
                s.full_name AS staff_name,
                o.own_office_id,
                o.version,
                o.state
            FROM orders o
            LEFT OUTER JOIN companies c
                ON c.company_id = o.prime_constractor_id
                AND c.state = ?
            LEFT OUTER JOIN offices co
                ON co.office_id = o.prime_constractor_office_id
                AND co.state = ?
            LEFT OUTER JOIN staffs s
                ON s.staff_id = o.staff_id
                AND s.state = ?
            WHERE (o.state = ? OR o.state = ?)
            AND (
                (o.%s >= ? AND o.%s < ?)
                OR o.%s IS NULL
            )
            AND (
                ? IS NULL
                OR o.own_office_id = ?
                OR (? = 0 AND o.own_office_id IS NULL)
            )
            """.formatted(column, column, column)
            + ACCESS_CONDITION;

        List<String> params = new java.util.ArrayList<>(List.of(
            "state",
            "state",
            "state",
            "state",
            "compState",
            "dateFrom",
            "dateTo",
            "ownOfficeId",
            "ownOfficeId",
            "ownOfficeId"
        ));

        params.addAll(ACCESS_PARAMS);

        return QueryDefinition.select(sql, params);
    }

    public static QueryDefinition orderDetail() {
        String sql = """
            SELECT
                o.order_id,
                o.request_number,
                o.visit_date,
                o.visit_time,
                o.title,
                o.postal_code,
                o.full_address,
                o.contact_information,
                o.remarks,
                o.prime_constractor_id,
                o.prime_constractor_office_id,
                o.staff_id,
                o.own_office_id,
                o.version,
                o.state
            FROM orders o
            WHERE o.state = ?
            AND o.order_id = ?
            """ + ACCESS_CONDITION;

        List<String> params = new java.util.ArrayList<>(List.of(
            "state",
            "orderId"
        ));

        params.addAll(ACCESS_PARAMS);

        return QueryDefinition.select(sql, params);
    }

    public static QueryDefinition orderHistoryList() {
        String sql = """
            WITH history_page AS (
                SELECT TOP (100) h.order_history_id, h.order_id, h.order_version,
                    h.event_type, h.editor, h.occurred_at, h.dispatch_assignment_id, h.dispatch_version
                FROM dbo.order_histories h
                JOIN dbo.orders o ON o.order_id = h.order_id
                WHERE o.state = ? AND o.order_id = ?
                    AND (? IS NULL OR h.order_history_id < ?)
                """ + ACCESS_CONDITION + """
                ORDER BY h.order_history_id DESC
            )
            SELECT h.*, previous.snapshot_count AS before_snapshot_count,
                current_log.snapshot_count AS after_snapshot_count,
                previous.request_number AS before_request_number, current_log.request_number AS after_request_number,
                previous.title AS before_title, current_log.title AS after_title,
                previous.postal_code AS before_postal_code, current_log.postal_code AS after_postal_code,
                previous.full_address AS before_full_address, current_log.full_address AS after_full_address,
                previous.visit_date AS before_visit_date, current_log.visit_date AS after_visit_date,
                previous.visit_time AS before_visit_time, current_log.visit_time AS after_visit_time,
                previous.contact_information AS before_contact_information, current_log.contact_information AS after_contact_information,
                previous.remarks AS before_remarks, current_log.remarks AS after_remarks,
                dispatch_before.snapshot_count AS before_dispatch_count,
                dispatch_after.snapshot_count AS after_dispatch_count,
                dispatch_before.daily_crew_id AS before_daily_crew_id,
                dispatch_after.daily_crew_id AS after_daily_crew_id,
                dispatch_before.visit_order AS before_visit_order,
                dispatch_after.visit_order AS after_visit_order
            FROM history_page h
            OUTER APPLY (
                SELECT TOP (1) COUNT(*) OVER () AS snapshot_count,
                    request_number, title, postal_code, full_address, CONVERT(char(10), visit_date, 23) AS visit_date, visit_time, contact_information, remarks
                FROM dbo.orders_log
                WHERE order_id = h.order_id AND version = h.order_version - 1
                    AND h.dispatch_assignment_id IS NULL
            ) previous
            OUTER APPLY (
                SELECT TOP (1) COUNT(*) OVER () AS snapshot_count,
                    request_number, title, postal_code, full_address, CONVERT(char(10), visit_date, 23) AS visit_date, visit_time, contact_information, remarks
                FROM dbo.orders_log
                WHERE order_id = h.order_id AND version = h.order_version
                    AND h.dispatch_assignment_id IS NULL
            ) current_log
            OUTER APPLY (
                SELECT TOP (1) COUNT(*) OVER () AS snapshot_count, daily_crew_id, visit_order
                FROM dbo.dispatch_assignments_log
                WHERE dispatch_assignment_id = h.dispatch_assignment_id AND order_id = h.order_id
                    AND version = h.dispatch_version - 1
            ) dispatch_before
            OUTER APPLY (
                SELECT TOP (1) COUNT(*) OVER () AS snapshot_count, daily_crew_id, visit_order
                FROM dbo.dispatch_assignments_log
                WHERE dispatch_assignment_id = h.dispatch_assignment_id AND order_id = h.order_id
                    AND version = h.dispatch_version
            ) dispatch_after
            ORDER BY h.order_history_id DESC
            """;
        List<String> params = new java.util.ArrayList<>(List.of(
            "state", "orderId", "beforeHistoryId", "beforeHistoryId"));
        params.addAll(ACCESS_PARAMS);
        return QueryDefinition.select(sql, params);
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
