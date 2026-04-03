package com.kyouseipro.neo.dto.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Enums.SqlMode;
import com.kyouseipro.neo.interfaces.LogSqlProvider;

public class SqlBuilder {
    public static String toSnake(String camel) {
        return camel
                .replaceAll("([a-z])([A-Z]+)", "$1_$2")
                .toLowerCase();
    }

    public static SqlResult buildSqlWithLog(
            String table,
            Map<String, Object> req,
            SqlMode mode,
            String idKey,
            String versionKey,
            LogSqlProvider logProvider
    ) {

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        List<String> sets = new ArrayList<>();

        String tableVar = switch (mode) {
            case INSERT -> "@InsertedRows";
            case UPDATE, DELETE -> "@UpdatedRows";
        };

        // ① ログテーブル
        sql.append(logProvider.buildLogTable(tableVar));

        if (mode == SqlMode.INSERT) {

            // ========================
            // INSERT
            // ========================
            List<String> columns = new ArrayList<>();
            List<String> values = new ArrayList<>();

            for (Map.Entry<String, Object> entry : req.entrySet()) {
                String key = entry.getKey();
                // ★除外
                if (key.equals(idKey) || key.equals("editor")) continue;

                columns.add(toSnake(entry.getKey()));
                values.add("?");
                params.add(entry.getValue());
            }

            sql.append("INSERT INTO ").append(table).append(" (");
            sql.append(String.join(", ", columns));
            sql.append(") ");

            sql.append(logProvider.buildOutput()).append(" INTO ").append(tableVar).append(" ");

            sql.append("VALUES (");
            sql.append(String.join(", ", values));
            sql.append(");");

        } else {

            // ========================
            // UPDATE / DELETE
            // ========================
            for (Map.Entry<String, Object> entry : req.entrySet()) {

                String key = entry.getKey();

                if (key.equals(idKey) || key.equals(versionKey) || key.equals("editor")) continue;

                sets.add(toSnake(key) + " = ?");
                params.add(entry.getValue());
            }

            if (mode == SqlMode.DELETE) {
                // 論理削除
                sets.add("state = ?");
                params.add(Enums.state.DELETE.getCode());
            }

            // 共通
            sets.add("version = version + 1");
            sets.add("update_date = SYSDATETIME()");

            sql.append("UPDATE ").append(table).append(" SET ");
            sql.append(String.join(", ", sets));

            sql.append(logProvider.buildOutput()).append(" INTO ").append(tableVar).append(" ");

            sql.append(" WHERE ")
            .append(toSnake(idKey)).append(" = ? AND ")
            .append(toSnake(versionKey)).append(" = ? AND NOT(state = ?);");

            params.add(req.get(idKey));
            params.add(req.get(versionKey));
            params.add(Enums.state.DELETE.getCode());
        }

        // ========================
        // ログ
        // ========================
        String action = mode.name();

        sql.append(logProvider.buildInsertLog(tableVar, action));

        // ★順番重要
        params.addAll(logProvider.buildLogParams(req, action));

        // ★snakeに変換
        sql.append("""
            SELECT %s FROM %s;
        """.formatted(toSnake(idKey), tableVar));

        return new SqlResult(sql.toString(), params);
    }

    public static <T> SqlResult buildDeleteByIds(
            List<T> ids,
            String editor,
            LogSqlProvider logProvider
    ){
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        String tableVar = "@Deleted";

        sql.append(logProvider.buildLogTable(tableVar));

        // placeholders
        String placeholders = ids.stream()
                .map(i -> "?")
                .collect(Collectors.joining(","));

        sql.append("UPDATE companies SET state = ? ");

        sql.append(logProvider.buildOutput()).append(" INTO ").append(tableVar).append(" ");

        sql.append("WHERE company_id IN (").append(placeholders).append(") ");
        sql.append("AND NOT(state = ?); ");

        // ★ params順序
        params.add(Enums.state.DELETE.getCode()); // state = ?
        params.addAll(ids);                       // IN (...)
        params.add(Enums.state.DELETE.getCode()); // NOT(state = ?)

        // ログ
        String action = "DELETE";
        sql.append(logProvider.buildInsertLog(tableVar, action));

        // ★ログparams
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("editor", editor);

        params.addAll(logProvider.buildLogParams(ctx, action));

        return new SqlResult(sql.toString(), params);
    }
}
