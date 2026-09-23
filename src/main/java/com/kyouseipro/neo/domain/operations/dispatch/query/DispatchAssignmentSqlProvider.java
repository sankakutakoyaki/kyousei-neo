package com.kyouseipro.neo.domain.operations.dispatch.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.query.operations.dispatch.DispatchAssignmentQuery;

@Component
public class DispatchAssignmentSqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {
        Map<QueryId, QueryDefinition> map = new HashMap<>();

        map.put(
            QueryId.DISPATCH_ASSIGNMENT_LIST,
            DispatchAssignmentQuery.dispatchAssignmentList()
        );

        map.put(
            QueryId.DISPATCH_ASSIGNMENT_NEXT_VISIT_ORDER,
            DispatchAssignmentQuery.dispatchAssignmentNextVisitOrder()
        );

        map.put(
            QueryId.DISPATCH_ASSIGNMENT_REORDER,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DISPATCH_ASSIGNMENT_REORDER, Tables.DISPATCH_ASSIGNMENT_BY_IDS)
        );

        map.put(
            QueryId.DISPATCH_ASSIGNMENT_DELETE_BY_IDS,
            new QueryDefinition(QueryType.UPDATE, QueryKind.DELETE_BY_IDS, Tables.DISPATCH_ASSIGNMENT_BY_IDS)
        );

        map.put(
            QueryId.DISPATCH_ASSIGNMENT_SAVE,
            new QueryDefinition(QueryType.UPDATE, QueryKind.SAVE, Tables.DISPATCH_ASSIGNMENT_BY_IDS)
        );

        return map;
    }
}