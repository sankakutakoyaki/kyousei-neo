package com.kyouseipro.neo.sql.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kyouseipro.neo.interfaces.LogSqlProvider;
import com.kyouseipro.neo.sql.model.SqlResult;

public class SqlService {
    public static String toSnake(String camel) {
        return camel
                .replaceAll("([a-z])([A-Z]+)", "$1_$2")
                .toLowerCase();
    }

    public static SqlResult buildInsertWithLog(String table, Map<String, Object> req, LogSqlProvider logProvider) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        // ログテーブル
        sql.append(logProvider.buildLogTable("@InsertedRows"));

        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();

        for (Map.Entry<String, Object> entry : req.entrySet()) {
            columns.add(toSnake(entry.getKey()));
            values.add("?");
            params.add(entry.getValue()); 
        }

        sql.append("INSERT INTO ").append(table).append(" (");
        sql.append(String.join(", ", columns));
        sql.append(") ");

        // OUTPUT
        sql.append(logProvider.buildOutput()).append(" INTO @InsertedRows ");

        sql.append("VALUES (");
        sql.append(String.join(", ", values));
        sql.append(");");

        // ログ書き込み
        String action = "INSERT";
        sql.append(logProvider.buildInsertLog("@InsertedRows", action));

        params.addAll(logProvider.buildLogParams(req, action));

        // ID返却
        sql.append("SELECT company_id FROM @InsertedRows;");

        return new SqlResult(sql.toString(), params);
    }

    public static SqlResult buildUpdateWithLog(
            String table,
            Map<String, Object> req,
            String idKey,
            String versionKey,
            LogSqlProvider logProvider
    ) {

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        List<String> sets = new ArrayList<>();

        // ① ログテーブル
        sql.append(logProvider.buildLogTable("@UpdatedRows"));

        // ② SET句
        for (Map.Entry<String, Object> entry : req.entrySet()) {
            String key = entry.getKey();
            if (key.equals(idKey) || key.equals(versionKey)) continue;

            String column = toSnake(key);
            sets.add(column + " = ?");
            params.add(entry.getValue()); // ← UPDATE用params
        }

        // 共通
        sets.add("version = version + 1");
        sets.add("update_date = SYSDATETIME()");

        // ③ UPDATE文
        sql.append("UPDATE ").append(table).append(" SET ");
        sql.append(String.join(", ", sets));

        // ④ OUTPUT（★修正①）
        sql.append(logProvider.buildOutput()).append(" INTO @UpdatedRows ");

        // ⑤ WHERE
        sql.append(" WHERE ")
        .append(toSnake(idKey)).append(" = ? AND ")
        .append(toSnake(versionKey)).append(" = ?;");

        params.add(req.get(idKey));
        params.add(req.get(versionKey));

        // ⑥ ログ
        String action = "UPDATE";
        sql.append(logProvider.buildInsertLog("@UpdatedRows", action));

        // ★修正②③（ここが最重要）
        params.addAll(logProvider.buildLogParams(req, action));

        return new SqlResult(sql.toString(), params);
    }
}