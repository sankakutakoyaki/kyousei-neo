package com.kyouseipro.neo.sql.query.company;

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
public class CompanySqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {
        Map<QueryId, QueryDefinition> map = new HashMap<>();

        // ===== Company =====
        map.put(QueryId.COMPANY_DETAIL, CompanyQuery.companyDetail());
        map.put(QueryId.COMPANY_LIST, CompanyQuery.companyList());
        map.put(QueryId.COMPANY_CSV, CompanyQuery.companyCsv());
        map.put(QueryId.COMPANY_DELETE_BY_IDS,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.COMPANY_BY_IDS));
        map.put(QueryId.COMPANY_SAVE,
            new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.COMPANY_BY_IDS));

        // ===== Client =====
        map.put(QueryId.CLIENT_LIST, CompanyQuery.clientList());

        return map;
    }
}