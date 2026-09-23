package com.kyouseipro.neo.sql.query.operations.dispatch;

import java.util.List;

import com.kyouseipro.neo.sql.model.QueryDefinition;

public class DispatchOrderQuery {

    private DispatchOrderQuery() {
    }

    public static QueryDefinition dispatchOrderList() {
        return QueryDefinition.select(
            """
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
                o.own_office_id,
                o.dispatch_category,
                o.version,
                o.state,

                COUNT(da.dispatch_assignment_id) AS assignment_count

            FROM orders o

            LEFT JOIN dispatch_assignments da
                ON da.order_id = o.order_id
                AND da.state = ?

            WHERE o.visit_date = ?
            AND o.own_office_id = ?
            AND o.dispatch_category = ?
            AND o.state = ?

            GROUP BY
                o.order_id,
                o.request_number,
                o.visit_date,
                o.visit_time,
                o.title,
                o.postal_code,
                o.full_address,
                o.contact_information,
                o.remarks,
                o.own_office_id,
                o.dispatch_category,
                o.version,
                o.state

            ORDER BY
                CASE
                    WHEN o.visit_time IS NULL THEN 1
                    ELSE 0
                END,
                o.visit_time,
                o.order_id
            """,
            List.of(
                "state",
                "workDate",
                "officeId",
                "dispatchCategory",
                "state"
            )
        );
    }
}