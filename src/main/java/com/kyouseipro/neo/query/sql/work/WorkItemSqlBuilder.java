package com.kyouseipro.neo.query.sql.work;

import com.kyouseipro.neo.common.Utilities;

public class WorkItemSqlBuilder {
    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (work_item_id INT, full_code INT, code INT, category_id INT, name NVARCHAR(255), version INT, state INT);";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
        return
            "INSERT INTO work_items_log (work_item_id, editor, process, log_date, full_code, code, category_id, name, version, state) " +
            "SELECT work_item_id, ?, '" + processName + "', CURRENT_TIMESTAMP, full_code, code, category_id, name, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.work_item_id, INSERTED.full_code, INSERTED.code, INSERTED.category_id, INSERTED.name, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsert(int index) {
        String rowTableName = "@InsertedRows" + index;
            return
                buildLogTable(rowTableName) +

                "INSERT INTO work_items (full_code, code, category_id, name, version, state) " +
                buildOutputLog() + "INTO " + rowTableName + " " +
                "SELECT " +
                "RIGHT('00' + CAST(c.code AS VARCHAR(2)), 2) + RIGHT('00' + CAST(? AS VARCHAR(2)), 2), " +
                "?, ?, ?, ?, ? " +
                "FROM work_item_categories c WHERE c.work_item_category_id = ?; " +

                buildInsertLog(rowTableName, "INSERT") +
                "SELECT work_item_id FROM " + rowTableName + ";";
    }

    public static String buildUpdate(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
                buildLogTable(rowTableName) +

                "UPDATE wi SET " +
                "full_code = LEFT(CAST(wi.full_code AS VARCHAR(4)), 2) " +
                "           + RIGHT('00' + CAST(? AS VARCHAR(2)), 2), " +
                "code=?, category_id=?, name=?, version=?, state=? " +

                buildOutputLog() + "INTO " + rowTableName + " " +

                "FROM work_items wi WHERE wi.work_item_id = ?; " +

                buildInsertLog(rowTableName, "UPDATE") +
                "SELECT work_item_id FROM " + rowTableName + ";";
    }

    public static String buildDelete(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTable(rowTableName) +
            "UPDATE work_items SET state = ? " +

            buildOutputLog() + "INTO " + rowTableName  + " " +
            "WHERE work_item_id = ? ; " +

            buildInsertLog(rowTableName, "DELETE") +
            "SELECT work_item_id FROM " + rowTableName + ";";
    }

    public static String buildDeleteByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"

        return
            buildLogTable("@DeletedRows") +
            "UPDATE work_items SET state = ? " +

            buildOutputLog() + "INTO @DeletedRows " +
            "WHERE work_item_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLog("@DeletedRows", "DELETE") +
            "SELECT work_item_id FROM @DeletedRows;";
    }

    private static String baseSelectString() {
        return
            "SELECT w.work_item_id, w.full_code, w.code, w.category_id, wc.code as category_code, wc.name as category_name, w.name, w.version, w.state FROM work_items w" +
            " LEFT OUTER JOIN work_item_categories wc ON wc.work_item_category_id = w.category_id AND NOT (wc.state = ?)";
    }

    public static String buildFindById() {
        return 
            baseSelectString() + " WHERE w.work_item_id = ? AND NOT (w.state = ?)";
    }

    public static String buildFindAll() {
        return 
            baseSelectString() + " WHERE NOT (w.state = ?) ORDER BY w.full_code";
    }

    public static String buildFindAllByCategoryId() {
        return 
            baseSelectString() + " WHERE w.category_id = ? AND NOT (w.state = ?) ORDER BY w.full_code";
    }

    public static String buildFindParentCategoryCombo() {
        return "SELECT work_item_category_id as number, name as text FROM work_item_categories WHERE NOT (state = ?) ORDER BY code;";
    }

    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return 
            baseSelectString() + " WHERE w.work_item_id IN (" + placeholders + ") AND NOT (w.state = ?)";
    }
}
