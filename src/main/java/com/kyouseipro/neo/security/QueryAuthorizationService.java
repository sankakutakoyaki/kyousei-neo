package com.kyouseipro.neo.security;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.model.TableMeta;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryAuthorizationService {

    private final PermissionService permissionService;

    public boolean isAllowed(Authentication authentication, QueryDefinition definition, SelectRequest request) {
        if (definition == null || definition.getKind() == null) {
            return false;
        }

        String operation = definition.getPermissionOperation();
        if (operation != null) {
            if (operation.isBlank()) return false;

            return permissionService.canOperation(authentication, operation);
        }

        QueryKind kind = definition.getKind();
        return switch (kind) {
            case INSERT -> canTable(authentication, definition, "insert");
            case UPDATE -> canTable(authentication, definition, "update");
            case DELETE_BY_IDS -> canTable(authentication, definition, "delete");
            case SAVE -> canSave(authentication, definition, request);
            case SQL -> canSql(authentication, definition);
            case CSV -> canRead(authentication, definition);
            case RECYCLE_PRICE_LIST, RECYCLE_PRICE_DETAIL ->
                definition.getType() == QueryType.SELECT
                    && canRead(authentication, definition);
            default -> false;
        };
    }

    private boolean canSave(Authentication authentication, QueryDefinition definition, SelectRequest request) {
        if (request == null || request.getParams() == null) {
            return false;
        }

        TableMeta meta = definition.getTableMeta();
        if (meta == null) return false;

        Map<String, Object> params = request.getParams();
        Object id = params.get(meta.idColumn());

        boolean insert;
        try {
            insert = id == null || Long.parseLong(id.toString()) == 0;
        } catch (NumberFormatException e) {
            return false;
        }
        return canTable(authentication, definition, insert ? "insert" : "update");
    }

    private boolean canTable(Authentication authentication, QueryDefinition definition, String action) {
        TableMeta meta = definition.getTableMeta();
        if (meta == null) return false;
        return permissionService.canTable(authentication, meta.tableName(), action);
    }

    private boolean canSql(Authentication authentication, QueryDefinition definition) {
        if (definition.getType() == QueryType.SELECT) {
            return canRead(authentication, definition);
        }
        return false;
    }

    private boolean canRead(Authentication authentication, QueryDefinition definition) {
        String table = definition.getPermissionTable();
        if (table == null || table.isBlank()) {
            return false;
        }
        return permissionService.canTable(authentication, table, "select");
    }
}