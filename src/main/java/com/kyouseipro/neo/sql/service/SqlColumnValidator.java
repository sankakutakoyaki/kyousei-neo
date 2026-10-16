package com.kyouseipro.neo.sql.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.sql.common.SqlBuilder;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SqlColumnValidator {

    private final QueryExecutor queryExecutor;
    private final Map<String, Set<String>> columnCache = new ConcurrentHashMap<>();

    public void validate(String tableName, Set<String> keys) {
        if (keys == null || keys.isEmpty()) return;

        // 先に項目名の形式を確認
        for (String key : keys) {
            SqlBuilder.toSnake(key);
        }

        Set<String> columns = columnCache.computeIfAbsent(tableName,
            name -> getColumns("dbo", name));

        for (String key : keys) {
            if (!columns.contains(SqlBuilder.toSnake(key))) {
                throw new IllegalArgumentException("存在しない項目です: " + key);
            }
        }
    }

    private Set<String> getColumns(String schemaName, String tableName) {
        QueryDefinition query = QueryDefinition.select("""
            SELECT c.name AS column_name
            FROM sys.columns c
            JOIN sys.tables t ON c.object_id = t.object_id
            JOIN sys.schemas s ON t.schema_id = s.schema_id
            WHERE s.name = ? AND t.name = ?
            """, List.of("schemaName", "tableName"));

        SelectRequest req = new SelectRequest();
        req.setParams(Map.of("schemaName", schemaName, "tableName", tableName));

        return queryExecutor.select(query, req).stream()
            .map(row -> (String) row.get("columnName"))
            .collect(Collectors.toUnmodifiableSet());
    }
}