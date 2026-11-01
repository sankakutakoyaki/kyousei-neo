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

                COUNT(da.dispatch_assignment_id) AS assignment_count,
                MAX(result_summary.completed_count) AS completed_count,
                MAX(result_summary.failed_count) AS failed_count,
                MAX(result_summary.postponed_count) AS postponed_count,
                MAX(result_summary.absent_count) AS absent_count,
                MAX(result_summary.unreported_count) AS unreported_count

            FROM orders o

            LEFT JOIN dispatch_assignments da
                ON da.order_id = o.order_id
                AND da.state = ?
                AND EXISTS (SELECT 1 FROM daily_crews assigned_crew WHERE assigned_crew.daily_crew_id=da.daily_crew_id
                    AND assigned_crew.work_date=o.visit_date AND assigned_crew.state=0)

            %s
            WHERE o.order_status <> 'CANCELLED' AND o.visit_date = ?
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
            """.formatted(WorkResultSummarySql.JOIN),
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
