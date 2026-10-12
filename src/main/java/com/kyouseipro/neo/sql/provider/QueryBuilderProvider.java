package com.kyouseipro.neo.sql.provider;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.interfaces.sql.QueryBuilder;
import com.kyouseipro.neo.interfaces.sql.QueryBuilderPart;

@Component
public class QueryBuilderProvider {
    private final Map<QueryId, QueryBuilder> builders;

    public QueryBuilderProvider(List<QueryBuilderPart> parts) {
        builders = QueryRegistrations.collect(parts, QueryBuilderPart::provide);
    }

    public QueryBuilder get(QueryId id){
        return id == null ? null : builders.get(id);
    }
}