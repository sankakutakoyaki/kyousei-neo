package com.kyouseipro.neo.domain.business.dispatch;

import java.util.*;
import org.springframework.stereotype.Component;
import com.kyouseipro.neo.common.enums.system.*;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.sql.model.QueryDefinition;

@Component
public class DispatchQueryProvider implements SqlProviderPart {
    public Map<QueryId, QueryDefinition> provide() {
        Map<QueryId, QueryDefinition> result = new HashMap<>();
        for (QueryId id : List.of(QueryId.DISPATCH_LIST, QueryId.DISPATCH_DETAIL,
                QueryId.DISPATCH_EMPLOYEE_LIST, QueryId.DISPATCH_SAVE)) {
            result.put(id, new QueryDefinition(id == QueryId.DISPATCH_SAVE ? QueryType.UPDATE : QueryType.SELECT,
                    QueryKind.DISPATCH, null));
        }
        return result;
    }
}
