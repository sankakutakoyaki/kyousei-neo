package com.kyouseipro.neo.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.sql.model.QueryDefinition;

@Component
public class QueryPermissionValidator {

    public List<String> validate(QueryDefinition def) {
        List<String> errors = new ArrayList<>();

        if (def == null || def.getKind() == null) {
            errors.add("QueryKindが設定されていません。");
            return errors;
        }

        if (def.getPermissionOperation() != null) {
            if (def.getPermissionOperation().isBlank()) {
                errors.add("permissionOperationが空です。");
            }
            return errors;
        }

        QueryKind kind = def.getKind();

        switch (kind) {
            case INSERT, UPDATE, SAVE, DELETE_BY_IDS -> {
                if (def.getTableMeta() == null) {
                    errors.add(kind + ": TableMetaが設定されていません。");
                }
            }

            case SQL -> {
                if (def.getType() == QueryType.SELECT) {
                    validatePermissionTable(def, errors);
                } else {
                    errors.add("SQL更新処理にはpermissionOperationが必要です。");
                }
            }

            case CSV -> validatePermissionTable(def, errors);

            case RECYCLE_PRICE_LIST, RECYCLE_PRICE_DETAIL -> {
                if (def.getType() != QueryType.SELECT) {
                    errors.add(kind + ": QueryType.SELECTが必要です。");
                } else {
                    validatePermissionTable(def, errors);
                }
            }

            default -> errors.add(
                kind + ": permissionOperationが設定されていません。"
            );
        }

        return errors;
    }

    private void validatePermissionTable(
            QueryDefinition def,
            List<String> errors) {

        if (def.getPermissionTable() == null
                || def.getPermissionTable().isBlank()) {
            errors.add("permissionTableが設定されていません。");
        }
    }
}