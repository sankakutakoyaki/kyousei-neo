package com.kyouseipro.neo.domain.master.api.work.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.query.master.work.WorkMasterQuery;

@Component
public class WorkMasterSqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {
        Map<QueryId, QueryDefinition> map = new HashMap<>();

        // ===== WorkMaster =====
        map.put(QueryId.WORK_MASTER_DETAIL, WorkMasterQuery.workMasterDetail());
        map.put(QueryId.WORK_MASTER_LIST, WorkMasterQuery.workMasterList());
        map.put(QueryId.WORK_MASTER_CSV, WorkMasterQuery.workMasterCsv());
        map.put(QueryId.WORK_MASTER_DELETE_BY_IDS,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.WORK_MASTER_BY_IDS));
        map.put(QueryId.WORK_MASTER_SAVE,
            new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.WORK_MASTER_BY_IDS));

        return map;
    }
}