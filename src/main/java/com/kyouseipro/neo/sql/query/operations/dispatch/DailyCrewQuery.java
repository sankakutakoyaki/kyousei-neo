package com.kyouseipro.neo.sql.query.operations.dispatch;

import java.util.List;

import com.kyouseipro.neo.sql.model.QueryDefinition;

public class DailyCrewQuery {

    public static QueryDefinition dailyCrewList() {
        return QueryDefinition.select(
            """
            SELECT
                dc.daily_crew_id,
                dc.work_date,
                dc.office_id,
                dc.dispatch_category,
                dc.vehicle_id,
                dc.display_order,
                dc.remarks,
                dc.version,
                dc.state,

                COALESCE(o.name, '') AS office_name,

                v.vehicle_name,
                v.manufacturer,
                v.model_code,
                v.registration_area,
                v.registration_class,
                v.registration_kana,
                v.registration_number

            FROM daily_crews dc

            LEFT OUTER JOIN offices o
                ON o.office_id = dc.office_id
                AND o.state = ?

            LEFT OUTER JOIN vehicles v
                ON v.vehicle_id = dc.vehicle_id
                AND v.state = ?

            WHERE dc.state = ?

            ORDER BY
                dc.work_date,
                dc.display_order,
                dc.daily_crew_id
            """,
            List.of("state", "state", "state")
        );
    }

    public static QueryDefinition dailyCrewDetail() {
        return QueryDefinition.select(
            """
            SELECT
                dc.daily_crew_id,
                dc.work_date,
                dc.office_id,
                dc.dispatch_category,
                dc.vehicle_id,
                dc.display_order,
                dc.remarks,
                dc.version,
                dc.state,

                COALESCE(o.name, '') AS office_name,

                v.vehicle_name,
                v.manufacturer,
                v.model_code,
                v.registration_area,
                v.registration_class,
                v.registration_kana,
                v.registration_number

            FROM daily_crews dc

            LEFT OUTER JOIN offices o
                ON o.office_id = dc.office_id
                AND o.state = ?

            LEFT OUTER JOIN vehicles v
                ON v.vehicle_id = dc.vehicle_id
                AND v.state = ?

            WHERE dc.state = ?
              AND dc.daily_crew_id = ?
            """,
            List.of("state", "state", "state", "dailyCrewId")
        );
    }

    public static QueryDefinition dailyCrewExists() {
        return QueryDefinition.select(
            """
            SELECT
                daily_crew_id
            FROM daily_crews
            WHERE work_date = ?
            AND office_id = ?
            AND dispatch_category = ?
            AND vehicle_id = ?
            AND state = ?
            """,
            List.of(
                "workDate",
                "officeId",
                "dispatchCategory",
                "vehicleId",
                "state"
            )
        );
    }

    public static QueryDefinition dailyCrewBoardList() {
        return QueryDefinition.select(
            """
            SELECT
                dc.daily_crew_id,
                dc.work_date,
                dc.office_id,
                dc.dispatch_category,
                dc.vehicle_id,
                dc.display_order,
                dc.remarks,

                v.vehicle_name,
                v.registration_area,
                v.registration_class,
                v.registration_kana,
                v.registration_number,
                v.manufacturer,
                v.model_code,

                dcm.daily_crew_member_id,
                dcm.employee_id,
                dcm.role,
                dcm.display_order AS member_display_order,

                COALESCE(e.full_name, '') AS employee_name,
                e.category AS employee_category,
                e.company_id,
                e.office_id AS employee_office_id,

                es.employee_shift_id,
                es.shift_type,
                es.start_time,
                es.end_time,
                es.office_id AS shift_office_id

            FROM daily_crews dc

            INNER JOIN vehicles v
                ON v.vehicle_id = dc.vehicle_id
                AND v.state = ?

            LEFT JOIN daily_crew_members dcm
                ON dcm.daily_crew_id = dc.daily_crew_id
                AND dcm.state = ?

            LEFT JOIN employees e
                ON e.employee_id = dcm.employee_id
                AND e.state = ?

            LEFT JOIN employee_shifts es
                ON es.employee_id = dcm.employee_id
                AND es.work_date = dc.work_date
                AND es.office_id = dc.office_id
                AND es.state = ?

            WHERE dc.work_date = ?
            AND dc.office_id = ?
            AND dc.dispatch_category = ?
            AND dc.state = ?

            ORDER BY
                dc.display_order,
                dc.daily_crew_id,
                dcm.role,
                dcm.display_order,
                dcm.daily_crew_member_id
            """,
            List.of(
                "state",
                "state",
                "state",
                "state",
                "workDate",
                "officeId",
                "dispatchCategory",
                "state"
            )
        );
    }
}