package com.kyouseipro.neo.domain.operations.shift.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.query.operations.shift.EmployeeShiftQuery;

@Component
public class EmployeeShiftSqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {

        Map<QueryId, QueryDefinition> map = new HashMap<>();

        map.put(
            QueryId.EMPLOYEE_SHIFT_DETAIL,
            EmployeeShiftQuery.employeeShiftDetail()
        );
        
        map.put(
            QueryId.EMPLOYEE_SHIFT_LIST,
            EmployeeShiftQuery.employeeShiftList()
        );

        map.put(
            QueryId.EMPLOYEE_SHIFT_MONTH_LIST,
            EmployeeShiftQuery.employeeShiftMonthList()
        );

        map.put(
            QueryId.EMPLOYEE_SHIFT_EMPLOYEE_LIST,
            EmployeeShiftQuery.employeeShiftEmployeeList()
        );

        map.put(
            QueryId.EMPLOYEE_SHIFT_WORKING_LIST,
            EmployeeShiftQuery.employeeShiftWorkingList()
        );

        map.put(
            QueryId.EMPLOYEE_SHIFT_DELETE_BY_IDS,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.EMPLOYEE_SHIFT_BY_IDS)
        );

        map.put(
            QueryId.EMPLOYEE_SHIFT_SAVE,
            new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.EMPLOYEE_SHIFT_BY_IDS)
        );

        return map;
    }
}