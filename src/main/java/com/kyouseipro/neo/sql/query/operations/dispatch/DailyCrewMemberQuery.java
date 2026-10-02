package com.kyouseipro.neo.sql.query.operations.dispatch;

import java.util.List;

import com.kyouseipro.neo.sql.model.QueryDefinition;

public class DailyCrewMemberQuery {

    public static QueryDefinition dailyCrewMemberList() {
        return QueryDefinition.select(
            """
            SELECT
                dcm.daily_crew_member_id,
                dcm.daily_crew_id,
                dcm.employee_id,
                dcm.role,
                dcm.display_order,
                dcm.version,
                dcm.state,

                COALESCE(e.full_name, '') AS employee_name,
                e.category,
                e.company_id,
                e.office_id

            FROM daily_crew_members dcm

            LEFT OUTER JOIN employees e
                ON e.employee_id = dcm.employee_id
                AND e.state = ?

            WHERE dcm.state = ?
              AND dcm.daily_crew_id = ?

            ORDER BY
                dcm.role,
                dcm.display_order,
                dcm.daily_crew_member_id
            """,
            List.of(
                "state",
                "state",
                "dailyCrewId"
            )
        );
    }

    public static QueryDefinition dailyCrewMemberExists() {
        return QueryDefinition.select(
            """
            SELECT
                daily_crew_member_id,
                daily_crew_id,
                employee_id,
                role,
                display_order,
                version,
                state

            FROM daily_crew_members

            WHERE daily_crew_id = ?
            AND employee_id = ?
            """,
            List.of(
                "dailyCrewId",
                "employeeId"
            )
        );
    }

    public static QueryDefinition dailyCrewMemberMaxDisplayOrder() {
        return QueryDefinition.select(
            """
            SELECT
                MAX(display_order) AS max_display_order

            FROM daily_crew_members

            WHERE daily_crew_id = ?
            AND state = ?
            """,
            List.of(
                "dailyCrewId",
                "state"
            )
        );
    }

    public static QueryDefinition dailyCrewMemberSaveList() {
        return QueryDefinition.select(
            """
            SELECT
                daily_crew_member_id,
                daily_crew_id,
                employee_id,
                role,
                display_order,
                version,
                state

            FROM daily_crew_members

            WHERE daily_crew_id = ?

            ORDER BY
                display_order,
                daily_crew_member_id
            """,
            List.of(
                "dailyCrewId"
            )
        );
    }
}