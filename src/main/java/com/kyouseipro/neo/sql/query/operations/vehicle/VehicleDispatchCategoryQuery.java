package com.kyouseipro.neo.sql.query.operations.vehicle;

import java.util.List;

import com.kyouseipro.neo.sql.model.QueryDefinition;

public class VehicleDispatchCategoryQuery {

    private VehicleDispatchCategoryQuery() {
    }

    public static QueryDefinition vehicleDispatchCategoryList() {
        return QueryDefinition.select(
            """
            SELECT
                vdc.vehicle_dispatch_category_id,
                vdc.vehicle_id,
                vdc.dispatch_category,
                vdc.version,
                vdc.state

            FROM vehicle_dispatch_categories vdc

            WHERE vdc.vehicle_id = ?
              AND vdc.state = ?

            ORDER BY
                vdc.dispatch_category,
                vdc.vehicle_dispatch_category_id
            """,
            List.of(
                "vehicleId",
                "state"
            )
        );
    }
}