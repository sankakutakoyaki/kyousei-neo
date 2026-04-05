package com.kyouseipro.neo.sql.provider;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.query.company.CompanyQuery;
import com.kyouseipro.neo.sql.query.employee.EmployeeQuery;

@Component
public class SqlProvider {
    private final Map<String, QueryDefinition> queries = new HashMap<>();

    public SqlProvider() {

        queries.put("partnerCompanyDetail", CompanyQuery.partnerCompanyDetail());
        queries.put("partnerEmployeeDetail", EmployeeQuery.partnerEmployeeDetail());
        queries.put("partnerCompanyList", CompanyQuery.partnerCompanyList());
        queries.put("partnerEmployeeList", EmployeeQuery.partnerEmployeeList());

    }

    public QueryDefinition get(String queryId) {
        QueryDefinition def = queries.get(queryId);
        if (def == null) {
            throw new IllegalArgumentException("未定義のqueryId: " + queryId);
        }
        return def;
    }
}