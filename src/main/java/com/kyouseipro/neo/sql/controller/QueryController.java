package com.kyouseipro.neo.sql.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.common.enums.system.SqlMode;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.interfaces.sql.QueryBuilder;
import com.kyouseipro.neo.sql.common.QueryParamBinder;
import com.kyouseipro.neo.sql.common.SqlBuilder;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.QueryBuilderProvider;
import com.kyouseipro.neo.sql.provider.QueryHandlerProvider;
import com.kyouseipro.neo.sql.provider.SqlProvider;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.sql.service.CsvService;
import com.kyouseipro.neo.sql.service.SqlColumnValidator;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QueryController {

    private final SqlProvider sqlProvider;
    private final QueryParamBinder paramBinder;
    private final SqlRepository sqlRepository;
    private final BaseSqlRepository baseRepository;
    private final CsvService csvService;
    private final QueryBuilderProvider queryBuilderProvider;
    private final QueryHandlerProvider handlerProvider;
    private final SqlColumnValidator columnValidator;

    @PostMapping("/query")
    public Object request(@RequestBody SelectRequest req) {
        QueryId queryId = QueryId.from(req.getQueryId());
        QueryBuilder builder = queryBuilderProvider.get(queryId);
        QueryDefinition def;

        if (builder != null) {
            def = builder.build(req);
        } else {
            def = sqlProvider.get(queryId);
        }

        return switch (def.getKind()) {
            case SQL -> executeSql(def, req);
            case SAVE -> executeSave(def, req);
            case INSERT -> executeInsert(def, req);
            case UPDATE -> executeUpdate(def, req);
            case DELETE_BY_IDS -> executeDeleteByIds(def, req);
            case CSV -> executeCsv(def, req);
            default -> executeHandler(def, req);
        };
    }

    // SQL
    private Object executeSql(QueryDefinition def, SelectRequest req) {
        if (def.getType() == QueryType.SELECT) {
            return Map.of("data", select(def, req));
        }
        List<Object> params = paramBinder.build(def.getParamOrder(), req.getParams());
        int count = sqlRepository.update(def.getSql(), params);
        return Map.of("count", count);
    }

    private List<Map<String, Object>> select(QueryDefinition def, SelectRequest req) {
        List<Object> params = paramBinder.build(def.getParamOrder(), req.getParams());
        return sqlRepository.selectMap(def.getSql(), params);
    }

    private Object executeSave(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        Object idValue = params.get(def.getTableMeta().idColumn());
        boolean isInsert = idValue == null || Long.valueOf(idValue.toString()) == 0;
        String editor = currentEditor();

        Map<String, Object> orderParams = new HashMap<>(params);
        orderParams.remove("items");
        orderParams = prepareSaveParams(def, orderParams, isInsert);

        if (isInsert) {
            Long id = baseRepository.insert(def.getTableMeta(), orderParams, editor);
            return Map.of("data", id, "count", 1);
        }

        int count = baseRepository.update(def.getTableMeta(), orderParams, editor);
        return Map.of("data", idValue, "count", count);
    }

    // INSERT
    private Object executeInsert(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = prepareSaveParams(def, req.getParams(), true);
        String editor = currentEditor();

        Long id = baseRepository.insert(def.getTableMeta(), params, editor);
        return Map.of("data", id);
    }

    // UPDATE
    private Object executeUpdate(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = prepareSaveParams(def, req.getParams(), false);
        String editor = currentEditor();

        int count = baseRepository.update(def.getTableMeta(), params, editor);
        return Map.of("count", count);
    }

    // DELETE_BY_IDS
    private Object executeDeleteByIds(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        String editor = currentEditor();
        int count = baseRepository.deleteByIds(def.getTableMeta(), ids, editor);
        return Map.of("count", count);
    }

    // CSV
    private Object executeCsv(QueryDefinition def, SelectRequest req) {
        return csvService.execute(def, req);
    }

    private Map<String, Object> prepareSaveParams(QueryDefinition def, Map<String, Object> source, boolean insert) {
        Map<String, Object> params = new HashMap<>(source);
        SqlBuilder.validateSystemColumns(params, insert ? SqlMode.INSERT : SqlMode.UPDATE);

        if (insert) {
            params.remove("state");
            params.put("state", State.INITIAL.getCode());
            params.remove("version");
        }

        validateColumns(def, params);
        return params;
    }

    private void validateColumns(QueryDefinition def, Map<String, Object> params) {
        String idKey = def.getTableMeta().idColumn();
        String versionKey = def.getTableMeta().versionColumn();

        Set<String> keys = new java.util.HashSet<>(params.keySet());
        keys.remove(idKey);
        keys.remove(versionKey);
        keys.remove("editor");
        keys.remove("items");

        columnValidator.validate(def.getTableMeta().tableName(), keys);
    }

    private String currentEditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            throw new BusinessException("ログイン情報を確認できません。");
        }

        if (auth.getPrincipal() instanceof OidcUser user) {
            String preferred = user.getAttribute("preferred_username");
            if (preferred != null && !preferred.isBlank()) return preferred;
            if (user.getEmail() != null && !user.getEmail().isBlank()) return user.getEmail();
        }

        String name = auth.getName();
        if (name == null || name.isBlank()) {
            throw new BusinessException("ログイン情報を確認できません。");
        }
        return name;
    }

    private Object executeHandler(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = new HashMap<>(
            req.getParams() != null ? req.getParams() : Map.of()
        );

        params.put("editor", currentEditor());
        req.setParams(params);

        return handlerProvider.get(def.getKind()).execute(def, req);
    }
}