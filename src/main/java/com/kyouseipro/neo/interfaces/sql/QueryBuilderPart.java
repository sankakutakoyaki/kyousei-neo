package com.kyouseipro.neo.interfaces.sql;

import java.util.Map;

import com.kyouseipro.neo.common.enums.system.QueryId;

public interface QueryBuilderPart {
    Map<QueryId, QueryBuilder> provide();
}