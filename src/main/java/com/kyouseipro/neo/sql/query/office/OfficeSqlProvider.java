package com.kyouseipro.neo.sql.query.office;

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
public class OfficeSqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {
        Map<QueryId, QueryDefinition> map = new HashMap<>();

        // ===== Office =====
        map.put(QueryId.OFFICE_DETAIL, OfficeQuery.officeDetail());
        map.put(QueryId.OFFICE_LIST, OfficeQuery.officeList());
        map.put(QueryId.OFFICE_CSV, OfficeQuery.officeCsv());
        map.put(QueryId.OFFICE_DELETE_BY_IDS,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.OFFICE_BY_IDS));
        map.put(QueryId.OFFICE_SAVE,
            new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.OFFICE_BY_IDS));

        // ===== Client =====
        map.put(QueryId.CLIENT_OFFICE_LIST, OfficeQuery.clientOfficeList());

        return map;
    }
}