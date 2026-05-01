package com.kyouseipro.neo.sql.query.recycle;

import java.util.HashMap;
import java.util.Map;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.provider.Tables;

public class RecycleSqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {
        Map<QueryId, QueryDefinition> map = new HashMap<>();

        // ===== Recycle =====
        map.put(QueryId.RECYCLE_DETAIL, RecycleQuery.recycleDetail());
        map.put(QueryId.RECYCLE_CSV, RecycleQuery.recycleCsv());
        map.put(QueryId.RECYCLE_DELETE_BY_IDS,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.RECYCLE_BY_IDS));
        map.put(QueryId.RECYCLE_SAVE,
            new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.RECYCLE_BY_IDS));

        return map;
    }
}