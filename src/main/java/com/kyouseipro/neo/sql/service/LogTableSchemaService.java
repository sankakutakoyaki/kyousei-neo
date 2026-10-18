package com.kyouseipro.neo.sql.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.sql.service.SqlTableMetadataService.ColumnMeta;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogTableSchemaService {

    private final SqlTableMetadataService metadataService;

    private static final Set<String> LOG_COLUMNS =
        Set.of("editor", "process", "log_date");

    public String createLogTableSql(String tableName) {
        List<ColumnMeta> columns = metadataService.getColumns(tableName);

        if (tableName.endsWith("_log")) {
            throw new IllegalArgumentException("ログテーブルは指定できません: " + tableName);
        }

        for (ColumnMeta column : columns) {
            if (LOG_COLUMNS.contains(column.name().toLowerCase(Locale.ROOT))) {
                throw new IllegalStateException(
                    "ログ専用カラムと名前が重複しています: " + column.name()
                );
            }
        }

        String definitions = columns.stream()
            .map(this::columnDefinition)
            .collect(Collectors.joining(",\n    "));

        return """
            CREATE TABLE [dbo].[%s_log] (
                [editor] NVARCHAR(255) NOT NULL,
                [process] NVARCHAR(50) NOT NULL,
                [log_date] DATETIME2(7) NOT NULL,
                %s
            );
            """.formatted(tableName, definitions);
    }

    private String columnDefinition(ColumnMeta column) {
        return "[" + column.name() + "] "
            + sqlType(column)
            + (column.nullable() ? " NULL" : " NOT NULL");
    }

    private String sqlType(ColumnMeta column) {
        String type = column.dataType().toUpperCase(Locale.ROOT);

        return switch (type) {
            case "NVARCHAR", "NCHAR" ->
                type + "(" + length(column.maxLength(), true) + ")";

            case "VARCHAR", "CHAR", "VARBINARY", "BINARY" ->
                type + "(" + length(column.maxLength(), false) + ")";

            case "DECIMAL", "NUMERIC" ->
                type + "(" + column.precision() + "," + column.scale() + ")";

            case "DATETIME2", "DATETIMEOFFSET", "TIME" ->
                type + "(" + column.scale() + ")";

            case "FLOAT" ->
                type + "(" + column.precision() + ")";

            case "INT", "BIGINT", "SMALLINT", "TINYINT",
                 "BIT", "DATE", "DATETIME", "SMALLDATETIME",
                 "REAL", "MONEY", "SMALLMONEY",
                 "UNIQUEIDENTIFIER" -> type;

            default -> throw new IllegalStateException(
                "ログテーブル生成で未対応のデータ型です: "
                + column.name() + " (" + type + ")"
            );
        };
    }

    private String length(int maxLength, boolean unicode) {
        if (maxLength == -1) return "MAX";
        return String.valueOf(unicode ? maxLength / 2 : maxLength);
    }

    public String addMissingColumnsSql(String tableName) {
        if (tableName == null || tableName.endsWith("_log")) {
            throw new IllegalArgumentException("不正なテーブル名です: " + tableName);
        }

        List<ColumnMeta> mainColumns = metadataService.getColumns(tableName);
        List<ColumnMeta> logColumns = metadataService.getColumns(tableName + "_log");

        Set<String> existing = logColumns.stream()
            .map(c -> c.name().toLowerCase(Locale.ROOT))
            .collect(Collectors.toSet());

        return mainColumns.stream()
            .filter(c -> !existing.contains(c.name().toLowerCase(Locale.ROOT)))
            .map(c -> "ALTER TABLE [dbo].[" + tableName + "_log] ADD ["
                + c.name() + "] " + sqlType(c) + " NULL;")
            .collect(Collectors.joining("\n"));
    }

    public List<String> findColumnMismatches(String tableName) {
        List<ColumnMeta> mainColumns = metadataService.getColumns(tableName);
        List<ColumnMeta> logColumns = metadataService.getColumns(tableName + "_log");

        Map<String, ColumnMeta> logMap = logColumns.stream()
            .collect(Collectors.toMap(
                c -> c.name().toLowerCase(Locale.ROOT),
                c -> c
            ));

        return mainColumns.stream()
            .filter(main -> {
                ColumnMeta log = logMap.get(main.name().toLowerCase(Locale.ROOT));
                if (log == null) return false; // 不足カラムは別メソッドで対応

                return !main.dataType().equalsIgnoreCase(log.dataType())
                    || main.maxLength() != log.maxLength()
                    || main.precision() != log.precision()
                    || main.scale() != log.scale()
                    || (main.nullable() && !log.nullable());
            })
            .map(ColumnMeta::name)
            .toList();
    }

    public record LogSchemaDiagnosis(
        String tableName,
        boolean logTableExists,
        String createSql,
        String alterSql,
        List<String> mismatches
    ) {}

    public LogSchemaDiagnosis diagnose(String tableName) {
        // 毎回、最新のテーブル定義を取得する
        metadataService.clearLogSchemaCache(tableName);

        if (!metadataService.hasLogTable(tableName)) {
            return new LogSchemaDiagnosis(
                tableName,
                false,
                createLogTableSql(tableName),
                "",
                List.of()
            );
        }

        return new LogSchemaDiagnosis(
            tableName,
            true,
            "",
            addMissingColumnsSql(tableName),
            java.util.stream.Stream.concat(
                findColumnMismatches(tableName).stream(),
                findLogColumnIssues(tableName).stream()
            ).toList()
        );
    }

    private List<String> findLogColumnIssues(String tableName) {
        Map<String, ColumnMeta> columns = metadataService.getColumns(tableName + "_log")
            .stream()
            .collect(Collectors.toMap(
                c -> c.name().toLowerCase(Locale.ROOT),
                c -> c
            ));

        List<String> issues = new java.util.ArrayList<>();

        checkLogColumn(columns, "editor", "nvarchar", 255, issues);
        checkLogColumn(columns, "process", "nvarchar", 50, issues);
        checkLogColumn(columns, "log_date", "datetime2", 0, issues);

        return issues;
    }

    private void checkLogColumn(
            Map<String, ColumnMeta> columns,
            String name,
            String expectedType,
            int minLength,
            List<String> issues) {

        ColumnMeta column = columns.get(name);

        if (column == null) {
            issues.add(name + ": カラムがありません");
            return;
        }

        if (!column.dataType().equalsIgnoreCase(expectedType)) {
            issues.add(name + ": データ型が一致しません");
            return;
        }

        if (minLength > 0 && column.maxLength() != -1
                && column.maxLength() < minLength * 2) {
            issues.add(name + ": 文字列長が不足しています");
        }
    }
}