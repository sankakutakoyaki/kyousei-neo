package com.kyouseipro.neo.sql.query.employee;

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
public class EmployeeSqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {
        Map<QueryId, QueryDefinition> map = new HashMap<>();

        // ===== Employee =====
        map.put(QueryId.EMPLOYEE_DETAIL, EmployeeQuery.employeeDetail());
        map.put(QueryId.EMPLOYEE_LIST, EmployeeQuery.employeeList());
        map.put(QueryId.EMPLOYEE_CSV, EmployeeQuery.employeeCsv());
        map.put(QueryId.EMPLOYEE_DELETE_BY_IDS,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.EMPLOYEE_BY_IDS));
        map.put(QueryId.EMPLOYEE_SAVE,
            new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.EMPLOYEE_BY_IDS));

        return map;
    }
}