package com.kyouseipro.neo.sql.query.operations.vehicle;

import java.util.List;

import com.kyouseipro.neo.sql.model.QueryDefinition;

public class VehicleDefaultMemberQuery {

    private VehicleDefaultMemberQuery() {
    }

    public static QueryDefinition vehicleDefaultMemberList() {
        return QueryDefinition.select(
            """
            SELECT
                vdm.vehicle_default_member_id,
                vdm.vehicle_id,
                vdm.employee_id,
                vdm.role,
                vdm.display_order,
                vdm.dispatch_category,
                vdm.version,
                vdm.state,

                COALESCE(e.full_name, '') AS employee_name,
                e.category AS employee_category,
                e.company_id,
                e.office_id AS employee_office_id

            FROM vehicle_default_members vdm

            INNER JOIN employees e
                ON e.employee_id = vdm.employee_id
                AND e.state = ?

            WHERE vdm.vehicle_id = ?
                AND vdm.dispatch_category = ?
                AND vdm.state = ?

            ORDER BY
                vdm.role,
                vdm.display_order,
                vdm.vehicle_default_member_id
            """,
            List.of(
                "state",
                "vehicleId",
                "dispatchCategory",
                "state"
            )
        );
    }

    public static QueryDefinition vehicleDefaultMemberWorkingList() {
        return QueryDefinition.select(
            """
            SELECT
                vdm.vehicle_default_member_id,
                vdm.vehicle_id,
                vdm.employee_id,
                vdm.role,
                vdm.display_order,
                vdm.dispatch_category,

                COALESCE(e.full_name, '') AS employee_name,

                es.employee_shift_id,
                es.work_date,
                es.office_id,
                es.shift_type,
                es.start_time,
                es.end_time

            FROM vehicle_default_members vdm

            INNER JOIN employees e
                ON e.employee_id = vdm.employee_id
                AND e.state = ?

            INNER JOIN employee_shifts es
                ON es.employee_id = vdm.employee_id
                AND es.work_date = ?
                AND es.office_id = ?
                AND es.shift_type = ?
                AND es.state = ?

            WHERE vdm.dispatch_category = ?
            AND vdm.state = ?

            ORDER BY
                vdm.vehicle_id,
                vdm.role,
                vdm.display_order,
                vdm.vehicle_default_member_id
            """,
            List.of(
                "state",
                "workDate",
                "officeId",
                "shiftType",
                "state",
                "dispatchCategory",
                "state"
            )
        );
    }

    public static QueryDefinition vehicleDispatchDefaultList() {
        return QueryDefinition.select(
            """
            SELECT
                v.vehicle_id,
                v.office_id,
                v.vehicle_name,

                v.registration_area,
                v.registration_class,
                v.registration_kana,
                v.registration_number,

                v.manufacturer,
                v.model_code,

                vdm.vehicle_default_member_id,
                vdm.dispatch_category,
                vdm.employee_id,
                vdm.role,
                vdm.display_order,

                COALESCE(e.full_name, '') AS employee_name,
                e.category AS employee_category,
                e.company_id,
                e.office_id AS employee_office_id,

                es.employee_shift_id,
                es.work_date,
                es.office_id AS shift_office_id,
                es.shift_type,
                es.start_time,
                es.end_time

            FROM vehicles v

            INNER JOIN vehicle_dispatch_categories vdc
                ON vdc.vehicle_id = v.vehicle_id
                AND vdc.dispatch_category = ?
                AND vdc.state = ?

            LEFT JOIN vehicle_default_members vdm
                ON vdm.vehicle_id = v.vehicle_id
                AND vdm.dispatch_category = ?
                AND vdm.state = ?

            LEFT JOIN employees e
                ON e.employee_id = vdm.employee_id
                AND e.state = ?

            LEFT JOIN employee_shifts es
                ON es.employee_id = vdm.employee_id
                AND es.work_date = ?
                AND es.office_id = ?
                AND es.state = ?

            WHERE v.office_id = ?
            AND v.state = ?

            ORDER BY
                v.vehicle_id,
                vdm.role,
                vdm.display_order,
                vdm.vehicle_default_member_id
            """,
            List.of(
                "dispatchCategory",
                "state",

                "dispatchCategory",
                "state",

                "state",

                "workDate",
                "officeId",
                "state",

                "officeId",
                "state"
            )
        );
    }
}