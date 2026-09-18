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
}