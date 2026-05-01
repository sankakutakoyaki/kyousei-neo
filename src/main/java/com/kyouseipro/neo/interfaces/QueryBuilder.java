package com.kyouseipro.neo.interfaces;

import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;

public interface QueryBuilder {
    QueryDefinition build(SelectRequest req);
}