package com.kyouseipro.neo.query.sql.document;

public class HistorySqlBuilder {
    public static String buildInsert() {
        return
            "DECLARE @InsertedRows TABLE (history_id INT)" +
            "INSERT INTO history (execution_date, user_name, table_name, action, result_code, result_message)" +
            "OUTPUT INSERTED.history_id INTO @InsertedRows " +
            " VALUES (CURRENT_TIMESTAMP,?,?,?,?,?);" +
            "SELECT history_id FROM @InsertedRows;";
    }
}
