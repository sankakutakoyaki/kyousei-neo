package com.kyouseipro.neo.sql.query.staff;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.provider.Tables;

@Component
public class StaffSqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {
        Map<QueryId, QueryDefinition> map = new HashMap<>();

        // ===== Staff =====
        map.put(QueryId.STAFF_DETAIL, StaffQuery.staffDetail());
        map.put(QueryId.STAFF_LIST, StaffQuery.staffList());
        map.put(QueryId.STAFF_CSV, StaffQuery.staffCsv());
        map.put(QueryId.STAFF_DELETE_BY_IDS,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.STAFF_BY_IDS));
        map.put(QueryId.STAFF_SAVE,
            new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.STAFF_BY_IDS));

        return map;
    }
}
