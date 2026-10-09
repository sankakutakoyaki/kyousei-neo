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
                e.category AS employee_category,

                e.employee_position_id,
                COALESCE(ep.position_name, '') AS position_name,
                ep.display_order AS position_display_order,

                c.company_id,
                c.name AS company_name,
                c.category AS company_category,

                STRING_AGG(
                    CAST(
                        m.employee_work_category_id
                        AS varchar(20)
                    ),
                    ','
                ) AS work_category_ids

            FROM employees e

            INNER JOIN employee_office_members eom
                ON eom.employee_id = e.employee_id
                AND eom.office_id = ?
                AND eom.state = ?

            LEFT JOIN companies c
                ON c.company_id = e.company_id
                AND c.state = ?

            LEFT JOIN employee_positions ep
                ON ep.employee_position_id = e.employee_position_id
                AND ep.state = ?

            LEFT JOIN employee_work_category_members m
                ON m.employee_id = e.employee_id
                AND m.state = ?

            WHERE e.state = ?

            GROUP BY
                e.employee_id,
                e.code,
                e.full_name,
                e.office_id,
                e.category,
                e.employee_position_id,
                ep.position_name,
                ep.display_order,
                c.company_id,
                c.name,
                c.category

                ORDER BY
                    /* 社員 → アルバイト → 協力会社 */
                    CASE
                        WHEN c.category = ?
                            AND e.category = ? THEN 0
                        WHEN c.category = ?
                            AND e.category = ? THEN 1
                        ELSE 2
                    END,

                    /* 部長～係長を先に */
                    CASE
                        WHEN ep.display_order < 50 THEN 0
                        ELSE 1
                    END,

                    /* 部長 → 次長 → 課長 → 係長 */
                    CASE
                        WHEN ep.display_order < 50
                        THEN ep.display_order
                        ELSE 9999
                    END,

                    /* 主任以下だけ 配送 → 工事 → 事務 */
                    CASE
                        WHEN ep.display_order >= 50
                            OR e.employee_position_id IS NULL
                        THEN
                            CASE
                                WHEN MAX(CASE
                                    WHEN m.employee_work_category_id = 1
                                    THEN 1 ELSE 0
                                END) = 1 THEN 1

                                WHEN MAX(CASE
                                    WHEN m.employee_work_category_id = 2
                                    THEN 1 ELSE 0
                                END) = 1 THEN 2

                                WHEN MAX(CASE
                                    WHEN m.employee_work_category_id = 3
                                    THEN 1 ELSE 0
                                END) = 1 THEN 3

                                ELSE 9
                            END
                        ELSE 0
                    END,

                    /* 配送等の中では主任 → 役職なし */
                    COALESCE(ep.display_order, 9999),

                    c.name,
                    e.full_name,
                    e.employee_id
            """,
            List.of(
                "officeId",
                "state",                 // eom
                "state",                 // company
                "state",                 // position
                "state",                 // work category member
                "state",                 // employee

                "ownCompanyCategory",
                "fulltimeCategory",
                "ownCompanyCategory",
                "parttimeCategory"
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

            INNER JOIN employee_office_members eom
                ON eom.employee_id = es.employee_id
            AND eom.office_id = ?
            AND eom.state = ?

            WHERE es.work_date >= ?
            AND es.work_date < ?
            AND es.state = ?

            ORDER BY
                es.employee_id,
                es.work_date
            """,
            List.of(
                "officeId",
                "state",
                "fromDate",
                "toDate",
                "state"
            )
        );
    }
}