package com.kyouseipro.neo.sql.provider;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.Enums.QueryKind;
import com.kyouseipro.neo.common.Enums.QueryType;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.query.company.CompanyQuery;
import com.kyouseipro.neo.sql.query.employee.EmployeeQuery;

@Component
public class SqlProvider {
    private final Map<String, QueryDefinition> map = new HashMap<>();

    public SqlProvider() {

        map.put("partnerCompanyDetail", CompanyQuery.partnerCompanyDetail());
        map.put("partnerEmployeeDetail", EmployeeQuery.partnerEmployeeDetail());
        map.put("partnerCompanyList", CompanyQuery.partnerCompanyList());
        map.put("partnerEmployeeList", EmployeeQuery.partnerEmployeeList());

        map.put("partnerCompanyCsv", CompanyQuery.partnerCompanyCsv());
        // map.put("partnerEmployeeCsv", EmployeeQuery.partnerEmployeeCsv());

        map.put("partnerCompanyDeleteByIds", new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.PARTNER_COMPANY));
        map.put("partnerEmployeeDeleteByIds", new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.PARTNER_EMPLOYEE));

        map.put("partnerCompanySave", new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.PARTNER_COMPANY));
        map.put("partnerEmployeeSave", new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.PARTNER_EMPLOYEE));
    }

    public QueryDefinition get(String queryId) {
        QueryDefinition def = map.get(queryId);
        if (def == null) {
            throw new IllegalArgumentException("未定義のqueryId: " + queryId);
        }
        return def;
    }
}