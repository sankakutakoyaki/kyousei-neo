package com.kyouseipro.neo.sql.query.operations.dispatch;

import java.util.List;

import com.kyouseipro.neo.sql.model.QueryDefinition;

public class DispatchEmployeeQuery {
    public static QueryDefinition dispatchEmployeeCandidateList() {
        return QueryDefinition.select(
            """
            SELECT
                e.employee_id,
                e.code,
                e.full_name,
                e.office_id,

                STRING_AGG(
                    CAST(
                        m.employee_work_category_id
                        AS varchar(20)
                    ),
                    ','
                ) AS work_category_ids,

                COALESCE(
                    MAX(es.shift_type),
                    0
                ) AS shift_type

            FROM employees e

            LEFT JOIN employee_work_category_members m
                ON m.employee_id = e.employee_id
            AND m.state = ?

            LEFT JOIN employee_shifts es
                ON es.employee_id = e.employee_id
            AND es.work_date = ?
            AND es.state = ?

            WHERE e.state = ?
            AND e.office_id = ?

            GROUP BY
                e.employee_id,
                e.code,
                e.full_name,
                e.office_id

            ORDER BY
                e.full_name,
                e.employee_id
            """,
            List.of(
                "state",
                "workDate",
                "state",
                "state",
                "officeId"
            )
        );
    }
}
