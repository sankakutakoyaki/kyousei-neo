package com.kyouseipro.neo.sql.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SqlTableMetadataService {

    private final QueryExecutor queryExecutor;
    private final Map<String, List<ColumnMeta>> cache = new ConcurrentHashMap<>();

    public record ColumnMeta(
        String name,
        String dataType,
        int maxLength,
        int precision,
        int scale,
        boolean nullable,
        boolean identity,
        boolean computed
    ) {}

    public List<ColumnMeta> getColumns(String tableName) {
        if (tableName == null || !tableName.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("不正なテーブル名です: " + tableName);
        }
        return cache.computeIfAbsent(tableName, this::loadColumns);
    }

    private List<ColumnMeta> loadColumns(String tableName) {
        QueryDefinition def = QueryDefinition.select("""
            SELECT
                c.name AS column_name,
                TYPE_NAME(c.system_type_id) AS data_type,
                c.max_length AS max_length,
                c.precision AS numeric_precision,
                c.scale AS numeric_scale,
                c.is_nullable AS is_nullable,
                c.is_identity AS is_identity,
                c.is_computed AS is_computed
            FROM sys.columns c
            JOIN sys.tables t ON c.object_id = t.object_id
            JOIN sys.schemas s ON t.schema_id = s.schema_id
            WHERE s.name = ? AND t.name = ?
            ORDER BY c.column_id
            """, List.of("schemaName", "tableName"));

        SelectRequest req = new SelectRequest();
        req.setParams(Map.of(
            "schemaName", "dbo",
            "tableName", tableName
        ));

        List<ColumnMeta> columns = queryExecutor.select(def, req).stream()
            .map(row -> new ColumnMeta(
                (String) row.get("columnName"),
                (String) row.get("dataType"),
                ((Number) row.get("maxLength")).intValue(),
                ((Number) row.get("numericPrecision")).intValue(),
                ((Number) row.get("numericScale")).intValue(),
                asBoolean(row.get("isNullable")),
                asBoolean(row.get("isIdentity")),
                asBoolean(row.get("isComputed"))
            ))
            .toList();

        if (columns.isEmpty()) {
            throw new IllegalArgumentException(
                "テーブルが見つからないか、カラム情報を取得できません: " + tableName
            );
        }
        return List.copyOf(columns);
    }

    private boolean asBoolean(Object value) {
        if (value instanceof Boolean b) return b;
        if (value instanceof Number n) return n.intValue() != 0;
        throw new IllegalArgumentException("不正なBIT値です: " + value);
    }

    public void validateLogTable(String tableName) {
        List<ColumnMeta> mainColumns = getColumns(tableName);
        List<ColumnMeta> logColumns = getColumns(tableName + "_log");

        Map<String, ColumnMeta> logMap = logColumns.stream()
            .collect(Collectors.toMap(ColumnMeta::name, c -> c));

        for (ColumnMeta main : mainColumns) {
            ColumnMeta log = logMap.get(main.name());

            if (log == null) {
                throw new IllegalStateException(
                    "Logテーブルにカラムがありません: " + tableName + "." + main.name()
                );
            }

            if (!main.dataType().equalsIgnoreCase(log.dataType())
                    || main.maxLength() != log.maxLength()
                    || main.precision() != log.precision()
                    || main.scale() != log.scale()) {
                throw new IllegalStateException(
                    "Logテーブルのカラム定義が一致しません: " + tableName + "." + main.name()
                );
            }

            if (main.nullable() && !log.nullable()) {
                throw new IllegalStateException(
                    "LogテーブルのNULL制約が厳しすぎます: " + tableName + "." + main.name()
                );
            }
        }
        validateLogColumn(logMap, "editor", "nvarchar", 255);
        validateLogColumn(logMap, "process", "nvarchar", 50);
        validateLogColumn(logMap, "log_date", "datetime2", 0);
    }

    private void validateLogColumn(
            Map<String, ColumnMeta> logMap,
            String name,
            String type,
            int minLength) {

        ColumnMeta column = logMap.get(name);

        if (column == null) {
            throw new IllegalStateException(
                "Logテーブルに必須カラムがありません: " + name
            );
        }

        if (!column.dataType().equalsIgnoreCase(type)) {
            throw new IllegalStateException(
                "Logテーブルのデータ型が不正です: " + name
            );
        }

        if (minLength > 0
                && column.maxLength() != -1
                && column.maxLength() < minLength * 2) {
            throw new IllegalStateException(
                "Logテーブルの文字列長が不足しています: " + name
            );
        }

        if (!column.nullable()) {
            // NOT NULLは問題ないため、ここではエラーにしない
        }
    }

    private final Map<String, Boolean> logTableCache = new ConcurrentHashMap<>();

    public boolean hasLogTable(String tableName) {
        if (tableName == null || !tableName.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("不正なテーブル名です: " + tableName);
        }

        return logTableCache.computeIfAbsent(tableName, this::checkLogTable);
    }

    private boolean checkLogTable(String tableName) {
        QueryDefinition def = QueryDefinition.select("""
            SELECT COUNT(*) AS table_count
            FROM sys.tables t
            JOIN sys.schemas s ON s.schema_id = t.schema_id
            WHERE s.name = ?
            AND t.name = ?
            """, List.of("schemaName", "tableName"));

        SelectRequest req = new SelectRequest();
        req.setParams(Map.of(
            "schemaName", "dbo",
            "tableName", tableName + "_log"
        ));

        List<Map<String, Object>> rows = queryExecutor.select(def, req);

        if (rows.isEmpty()) {
            throw new IllegalStateException("Logテーブルの存在確認に失敗しました: " + tableName);
        }

        Object count = rows.get(0).get("tableCount");

        if (!(count instanceof Number number)) {
            throw new IllegalStateException("Logテーブルの存在確認結果が不正です: " + tableName);
        }

        return number.intValue() > 0;
    }

    public void clearCache(String tableName) {
        if (tableName == null || !tableName.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("不正なテーブル名です: " + tableName);
        }

        cache.remove(tableName);
        logTableCache.remove(tableName);
    }

    public void clearLogSchemaCache(String tableName) {
        clearCache(tableName);
        clearCache(tableName + "_log");
    }

    public List<String> getTablesWithLog() {
        QueryDefinition def = QueryDefinition.select("""
            SELECT t.name AS table_name
            FROM sys.tables t
            JOIN sys.schemas s ON s.schema_id = t.schema_id
            JOIN sys.tables lt
                ON lt.schema_id = t.schema_id
                AND lt.name = t.name + '_log'
            WHERE s.name = ?
            AND t.name NOT LIKE '%[_]log'
            ORDER BY t.name
            """, List.of("schemaName"));

        SelectRequest req = new SelectRequest();
        req.setParams(Map.of("schemaName", "dbo"));

        return queryExecutor.select(def, req).stream()
            .map(row -> (String) row.get("tableName"))
            .toList();
    }
}