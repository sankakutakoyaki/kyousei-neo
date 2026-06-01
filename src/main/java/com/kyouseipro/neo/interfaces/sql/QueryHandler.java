package com.kyouseipro.neo.interfaces.sql;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;

public interface QueryHandler {
    boolean supports(QueryKind kind);
    Object execute(QueryDefinition def, SelectRequest req);
}