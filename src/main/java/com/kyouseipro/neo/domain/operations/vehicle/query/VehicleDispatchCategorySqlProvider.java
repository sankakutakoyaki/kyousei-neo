package com.kyouseipro.neo.domain.operations.vehicle.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.query.operations.vehicle.VehicleDispatchCategoryQuery;

@Component
public class VehicleDispatchCategorySqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {

        Map<QueryId, QueryDefinition> map = new HashMap<>();

        map.put(
            QueryId.VEHICLE_DISPATCH_CATEGORY_LIST,
            VehicleDispatchCategoryQuery.vehicleDispatchCategoryList()
        );

        map.put(
            QueryId.VEHICLE_DISPATCH_CATEGORY_DELETE_BY_IDS,
            new QueryDefinition(
                QueryType.UPDATE,
                QueryKind.DELETE_BY_IDS,
                Tables.VEHICLE_DISPATCH_CATEGORY_BY_IDS
            )
        );

        map.put(
            QueryId.VEHICLE_DISPATCH_CATEGORY_SAVE,
            new QueryDefinition(
                QueryType.UPDATE,
                QueryKind.SAVE,
                Tables.VEHICLE_DISPATCH_CATEGORY_BY_IDS
            )
        );

        return map;
    }
}