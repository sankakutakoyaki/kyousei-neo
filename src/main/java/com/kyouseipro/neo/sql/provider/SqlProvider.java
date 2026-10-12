package com.kyouseipro.neo.sql.provider;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.sql.model.QueryDefinition;

@Component
public class SqlProvider {
    private final Map<QueryId, QueryDefinition> map;

    public SqlProvider(List<SqlProviderPart> parts) {
        map = QueryRegistrations.collect(parts, SqlProviderPart::provide);
    }

    public QueryDefinition get(String queryId) {
        QueryId id = QueryId.from(queryId);
        return get(id);
    }

    public QueryDefinition get(QueryId id) {
        QueryDefinition def = id == null ? null : map.get(id);
        if (def == null) {
            throw new IllegalArgumentException("未定義のqueryId: " + id);
        }
        return def;
    }
}