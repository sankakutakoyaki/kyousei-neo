package com.kyouseipro.neo.sql.query.operations.shift;

import java.util.List;

import com.kyouseipro.neo.sql.model.QueryDefinition;

public class EmployeeShiftQuery {

    private EmployeeShiftQuery() {
    }

    /**
     * シフト詳細
     */
    public static QueryDefinition employeeShiftDetail() {
        return QueryDefinition.select(
            """
            SELECT
                es.employee_shift_id,
                es.work_date,
                es.employee_id,
                es.office_id,
                es.shift_type,
                es.start_time,
                es.end_time,
                es.remarks,
                es.version,
                es.state,

                COALESCE(e.full_name, '') AS employee_name

            FROM employee_shifts es

            LEFT JOIN employees e
                ON e.employee_id = es.employee_id
                AND e.state = ?

            WHERE es.employee_shift_id = ?
              AND es.state = ?
            """,
            List.of(
                "state",
                "employeeShiftId",
                "state"
            )
        );
    }

    /**
     * シフト一覧
     */
    public static QueryDefinition employeeShiftList() {
        return QueryDefinition.select(
            """
            SELECT
                es.employee_shift_id,
                es.work_date,
                es.employee_id,
                es.office_id,
                es.shift_type,
                es.start_time,
                es.end_time,
                es.remarks,
                es.version,
                es.state,

                COALESCE(e.full_name, '') AS employee_name,
                e.category AS employee_category

            FROM employee_shifts es

            LEFT JOIN employees e
                ON e.employee_id = es.employee_id
                AND e.state = ?

            WHERE es.state = ?

              AND (
                    ? IS NULL
                    OR es.work_date = ?
                  )

              AND (
                    ? IS NULL
                    OR es.office_id = ?
                  )

            ORDER BY
                es.work_date,
                es.office_id,
                e.full_name,
                es.employee_shift_id
            """,
            List.of(
                "state",
                "state",
                "workDate",
                "workDate",
                "officeId",
                "officeId"
            )
        );
    }

    /**
     * 指定日の出勤予定者
     *
     * 配車候補取得用
     */
    public static QueryDefinition employeeShiftWorkingList() {
        return QueryDefinition.select(
            """
            SELECT
                es.employee_shift_id,
                es.work_date,
                es.employee_id,
                es.office_id,
                es.shift_type,
                es.start_time,
                es.end_time,

                COALESCE(e.full_name, '') AS employee_name,
                e.category AS employee_category,
                e.company_id,
                e.office_id AS employee_office_id

            FROM employee_shifts es

            INNER JOIN employees e
                ON e.employee_id = es.employee_id
                AND e.state = ?

            WHERE es.work_date = ?
              AND es.office_id = ?
              AND es.shift_type = ?
              AND es.state = ?

            ORDER BY
                e.full_name,
                es.employee_shift_id
            """,
            List.of(
                "state",
                "workDate",
                "officeId",
                "shiftType",
                "state"
            )
        );
    }

    /**
     * シフト入力対象の従業員一覧
     */
    public static QueryDefinition employeeShiftEmployeeList() {
        return QueryDefinition.select(
            """
            SELECT
                e.employee_id,
                e.code,
                e.full_name,
                e.office_id,
                STRING_AGG(
                    CAST(m.employee_work_category_id AS varchar(20)),
                    ','
                ) AS work_category_ids
            FROM employees e
            LEFT JOIN employee_work_category_members m
                ON m.employee_id = e.employee_id
            AND m.state = ?
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
                "state",
                "officeId"
            )
        );
    }

    /**
     * 月間シフト一覧
     */
    public static QueryDefinition employeeShiftMonthList() {
        return QueryDefinition.select(
            """
            SELECT
                es.employee_shift_id,
                es.work_date,
                es.employee_id,
                es.office_id,
                es.shift_type,
                es.start_time,
                es.end_time,
                es.remarks,
                es.version,
                es.state

            FROM employee_shifts es

            WHERE es.work_date >= ?
            AND es.work_date < ?
            AND es.office_id = ?
            AND es.state = ?

            ORDER BY
                es.employee_id,
                es.work_date
            """,
            List.of(
                "fromDate",
                "toDate",
                "officeId",
                "state"
            )
        );
    }
}