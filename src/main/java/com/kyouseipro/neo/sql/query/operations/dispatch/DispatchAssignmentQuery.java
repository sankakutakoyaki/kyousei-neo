package com.kyouseipro.neo.sql.query.operations.dispatch;

import java.util.List;

import com.kyouseipro.neo.sql.model.QueryDefinition;

public class DispatchAssignmentQuery {

    public static QueryDefinition dispatchAssignmentList() {
        return QueryDefinition.select(
            """
            SELECT
                da.dispatch_assignment_id,
                da.order_id,
                da.daily_crew_id,
                da.visit_order,
                da.remarks,
                da.version,
                da.state,

                o.request_number,
                o.visit_date,
                o.visit_time,
                o.title,
                o.postal_code,
                o.full_address,
                o.contact_information,
                o.own_office_id

            FROM dispatch_assignments da

            LEFT OUTER JOIN orders o
                ON o.order_id = da.order_id
                AND o.state = ?

            WHERE da.state = ?
              AND da.daily_crew_id = ?

            ORDER BY
                da.visit_order,
                da.dispatch_assignment_id
            """,
            List.of(
                "state",
                "state",
                "dailyCrewId"
            )
        );
    }
}