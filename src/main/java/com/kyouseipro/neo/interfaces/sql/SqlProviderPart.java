package com.kyouseipro.neo.interfaces.sql;

import java.util.Map;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public interface SqlProviderPart {
    Map<QueryId, QueryDefinition> provide();
}
