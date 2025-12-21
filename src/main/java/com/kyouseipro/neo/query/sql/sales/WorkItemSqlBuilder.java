package com.kyouseipro.neo.query.sql.sales;

import com.kyouseipro.neo.common.Utilities;

public class WorkItemSqlBuilder {
    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (work_item_id INT, code INT, category INT, name NVARCHAR(255), version INT, state INT);";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO work_items_log (work_item_id, editor, process, log_date, code, category, name, version, state) " +
            "SELECT work_item_id, ?, '" + processName + "', CURRENT_TIMESTAMP, code, categirtm, name, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.work_item_id, INSERTED.code, INSERTED.category, INSERTED.name, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertWorkContentSql(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTableSql(rowTableName) +
            "INSERT INTO work_items (code, category, name, version, state) " +

            buildOutputLogSql() + "INTO " + rowTableName + " " +
            "VALUES (?, ?, ?, ?, ?); " +

            buildInsertLogSql(rowTableName, "INSERT") +
            "SELECT work_item_id FROM " + rowTableName + ";";
    }

    public static String buildUpdateWorkContentSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +
            "UPDATE work_items SET code=?, category=?, name=?, version=?, state=? " +
            
            buildOutputLogSql() + "INTO " + rowTableName + " " +
            "WHERE work_item_id=?; " +

            buildInsertLogSql(rowTableName, "UPDATE") +
            "SELECT work_item_id FROM " + rowTableName + ";";
    }

    public static String buildDeleteWorkContentSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +
            "UPDATE work_items SET state = ? " +

            buildOutputLogSql() + "INTO " + rowTableName  + " " +
            "WHERE work_item_id = ? ; " +

            buildInsertLogSql(rowTableName, "DELETE") +
            "SELECT work_item_id FROM " + rowTableName + ";";
    }

    private static String baseSelectString() {
        return
            "SELECT w.work_item_id, w.code, w.category, w.name, w.version, w.state FROM work_items w";
    }

    public static String buildFindByIdSql() {
        return 
            baseSelectString() + " WHERE w.work_item_id = ? AND NOT (w.state = ?)";
    }

    public static String buildFindAllSql() {
        return 
            baseSelectString() + " WHERE NOT (w.state = ?)";
    }

    public static String buildFindAllByCategoryIdSql() {
        return 
            baseSelectString() + " WHERE w.category_id = ? AND NOT (w.state = ?)";
    }
}
