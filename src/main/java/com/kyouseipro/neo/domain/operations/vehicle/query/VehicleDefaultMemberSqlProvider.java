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
import com.kyouseipro.neo.sql.query.operations.vehicle.VehicleDefaultMemberQuery;

@Component
public class VehicleDefaultMemberSqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {

        Map<QueryId, QueryDefinition> map = new HashMap<>();

        map.put(
            QueryId.VEHICLE_DEFAULT_MEMBER_LIST,
            VehicleDefaultMemberQuery.vehicleDefaultMemberList()
        );

        map.put(
            QueryId.VEHICLE_DEFAULT_MEMBER_WORKING_LIST,
            VehicleDefaultMemberQuery.vehicleDefaultMemberWorkingList()
        );

        map.put(
            QueryId.VEHICLE_DISPATCH_DEFAULT_LIST,
            VehicleDefaultMemberQuery.vehicleDispatchDefaultList()
        );

        map.put(
            QueryId.VEHICLE_DEFAULT_MEMBER_DELETE_BY_IDS,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS)
        );

        map.put(
            QueryId.VEHICLE_DEFAULT_MEMBER_SAVE,
            new QueryDefinition(QueryType.UPDATE, QueryKind.VEHICLE_DEFAULT_MEMBER_SAVE, Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS)
        );

        return map;
    }
}