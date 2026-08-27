package com.kyouseipro.neo.domain.master.api.item.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.query.master.item.ItemMasterQuery;

@Component
public class ItemMasterSqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {
        Map<QueryId, QueryDefinition> map = new HashMap<>();

        // ===== ItemMaster =====
        map.put(QueryId.ITEM_MASTER_DETAIL, ItemMasterQuery.itemMasterDetail());
        map.put(QueryId.ITEM_MASTER_LIST, ItemMasterQuery.itemMasterList());
        map.put(QueryId.ITEM_MASTER_FIND_BY_JAN_CODE, ItemMasterQuery.findByJanCode());
        map.put(QueryId.ITEM_MASTER_CSV, ItemMasterQuery.itemMasterCsv());
        map.put(QueryId.ITEM_MASTER_DELETE_BY_IDS,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.ITEM_MASTER_BY_IDS));
        map.put(QueryId.ITEM_MASTER_SAVE,
            new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.ITEM_MASTER_BY_IDS));

        return map;
    }
}