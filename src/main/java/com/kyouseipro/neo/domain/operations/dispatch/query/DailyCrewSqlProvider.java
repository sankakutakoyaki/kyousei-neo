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
import com.kyouseipro.neo.sql.query.operations.dispatch.DailyCrewQuery;

@Component
public class DailyCrewSqlProvider implements SqlProviderPart {

    @Override
    public Map<QueryId, QueryDefinition> provide() {
        Map<QueryId, QueryDefinition> map = new HashMap<>();

        map.put(QueryId.DAILY_CREW_DETAIL, DailyCrewQuery.dailyCrewDetail());
        map.put(QueryId.DAILY_CREW_LIST, DailyCrewQuery.dailyCrewList());
        map.put(QueryId.DAILY_CREW_BOARD_LIST, DailyCrewQuery.dailyCrewBoardList());
        
        map.put(
            QueryId.DAILY_CREW_DELETE_BY_IDS,
            new QueryDefinition(
                QueryType.UPDATE,
                QueryKind.DELETE_BY_IDS,
                Tables.DAILY_CREW_BY_IDS
            )
        );

        map.put(
            QueryId.DAILY_CREW_SAVE,
            new QueryDefinition(
                QueryType.UPDATE,
                QueryKind.SAVE,
                Tables.DAILY_CREW_BY_IDS
            )
        );

        map.put(
            QueryId.DAILY_CREW_BULK_CREATE,
            new QueryDefinition(
                QueryType.UPDATE,
                QueryKind.DAILY_CREW_BULK_CREATE,
                Tables.DAILY_CREW_BY_IDS
            )
        );

        return map;
    }
}