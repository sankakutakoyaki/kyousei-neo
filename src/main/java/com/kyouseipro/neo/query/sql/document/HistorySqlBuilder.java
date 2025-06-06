package com.kyouseipro.neo.query.sql.document;

public class HistorySqlBuilder {
    public static String buildInsertHistorySql() {
        return
            "INSERT INTO history (execution_date, user_name, table_name, action, result_code, result_message)" +
            " VALUES (CURRENT_TIMESTAMP,?,?,?,?,?);" +
            "SELECT 200 as number, '' as text;";
    }
}
