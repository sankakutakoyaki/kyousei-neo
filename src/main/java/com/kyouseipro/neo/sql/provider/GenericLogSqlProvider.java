package com.kyouseipro.neo.sql.provider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;
import com.kyouseipro.neo.sql.service.SqlTableMetadataService;
import com.kyouseipro.neo.sql.service.SqlTableMetadataService.ColumnMeta;

public class GenericLogSqlProvider implements LogSqlProvider {

    private final String tableName;
    private final List<ColumnMeta> columns;

    public GenericLogSqlProvider(String tableName, SqlTableMetadataService metadataService) {
        metadataService.validateLogTable(tableName);
        this.tableName = tableName;
        this.columns = metadataService.getColumns(tableName);
    }

    @Override
    public String buildLogTable(String tableVar) {
        String definitions = columns.stream()
            .map(c -> "[" + c.name() + "] " + sqlType(c) + " NULL")
            .collect(Collectors.joining(",\n    "));

        return "DECLARE " + tableVar + " TABLE (\n    "
            + definitions + "\n);\n";
    }

    @Override
    public String buildOutput() {
        return "OUTPUT " + columns.stream()
            .map(c -> "INSERTED.[" + c.name() + "]")
            .collect(Collectors.joining(", "))
            + " ";
    }

    @Override
    public String buildInsertLog(String tableVar, String action) {
        String columnNames = columns.stream()
            .map(c -> "[" + c.name() + "]")
            .collect(Collectors.joining(", "));

        return """
            INSERT INTO [dbo].[%s_log] (
                [editor], [process], [log_date], %s
            )
            SELECT ?, ?, SYSDATETIME(), %s
            FROM %s;
            """.formatted(tableName, columnNames, columnNames, tableVar);
    }

    @Override
    public List<Object> buildLogParams(Map<String, Object> req, String action) {
        Object editor = req.get("editor");
        if (editor == null) {
            throw new IllegalArgumentException("editorが設定されていません");
        }
        return List.of(editor, action);
    }

    private String sqlType(ColumnMeta column) {
        String type = column.dataType().toUpperCase(java.util.Locale.ROOT);

        return switch (type) {
            case "NVARCHAR", "NCHAR" ->
                type + "(" + length(column.maxLength(), true) + ")";

            case "VARCHAR", "CHAR", "VARBINARY", "BINARY" ->
                type + "(" + length(column.maxLength(), false) + ")";

            case "DECIMAL", "NUMERIC" ->
                type + "(" + column.precision() + "," + column.scale() + ")";

            case "DATETIME2", "DATETIMEOFFSET", "TIME" ->
                type + "(" + column.scale() + ")";

            case "INT", "BIGINT", "SMALLINT", "TINYINT",
                "BIT", "DATE", "DATETIME", "SMALLDATETIME",
                "FLOAT", "REAL", "MONEY", "SMALLMONEY",
                "UNIQUEIDENTIFIER" -> type;

            default -> throw new IllegalStateException(
                "汎用ログで未対応のデータ型です: "
                    + tableName + "." + column.name() + " (" + type + ")"
            );
        };
    }

    private String length(int maxLength, boolean unicode) {
        if (maxLength == -1) return "MAX";
        return String.valueOf(unicode ? maxLength / 2 : maxLength);
    }
}