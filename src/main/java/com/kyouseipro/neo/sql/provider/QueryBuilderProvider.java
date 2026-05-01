package com.kyouseipro.neo.sql.provider;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.interfaces.QueryBuilder;
import com.kyouseipro.neo.sql.query.recycle.RecycleQuery;

@Component
public class QueryBuilderProvider {
    private final Map<QueryId, QueryBuilder> builders = new HashMap<>();

    public QueryBuilderProvider() {

        builders.put(QueryId.RECYCLE_LIST, req -> {
            String category = (String) req.getParams().get("category");
            return RecycleQuery.recycleList(category);
        });

        // 将来ここに追加していくだけ

    }

    public QueryBuilder get(QueryId id) {
        return builders.get(id);
    }
}