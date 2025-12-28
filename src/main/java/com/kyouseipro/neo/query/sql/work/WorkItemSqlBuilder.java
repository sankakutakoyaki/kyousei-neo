package com.kyouseipro.neo.query.sql.work;

import com.kyouseipro.neo.common.Utilities;

public class WorkItemSqlBuilder {
    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (work_item_id INT, full_code INT, code INT, category_id INT, name NVARCHAR(255), version INT, state INT);";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO work_items_log (work_item_id, editor, process, log_date, full_code, code, category_id, name, version, state) " +
            "SELECT work_item_id, ?, '" + processName + "', CURRENT_TIMESTAMP, full_code, code, category_id, name, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.work_item_id, INSERTED.full_code, INSERTED.code, INSERTED.category_id, INSERTED.name, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertWorkItemSql(int index) {
        String rowTableName = "@InsertedRows" + index;
            return
                buildLogTableSql(rowTableName) +

                "INSERT INTO work_items (full_code, code, category_id, name, version, state) " +
                buildOutputLogSql() + "INTO " + rowTableName + " " +
                "SELECT " +
                "RIGHT('00' + CAST(c.code AS VARCHAR(2)), 2) + RIGHT('00' + CAST(? AS VARCHAR(2)), 2), " +
                "?, ?, ?, ?, ? " +
                "FROM work_item_categories c WHERE c.work_item_category_id = ?; " +

                buildInsertLogSql(rowTableName, "INSERT") +
                "SELECT work_item_id FROM " + rowTableName + ";";
        // return
        //     buildLogTableSql(rowTableName) +
        //     "INSERT INTO work_items (full_code, code, category_id, name, version, state) " +
        //     "SELECT RIGHT('00' + CAST(c.code AS VARCHAR(2)), 2) + RIGHT('00' + CAST(? AS VARCHAR(2)), 2), ?, ?, ?, ?, ? " +
        //     "FROM work_item_categories c WHERE c.category_id = ?;" +

        //     buildOutputLogSql() + "INTO " + rowTableName + " " +
        //     "VALUES (?, ?, ?, ?, ?, ?); " +

        //     buildInsertLogSql(rowTableName, "INSERT") +
        //     "SELECT work_item_id FROM " + rowTableName + ";";
    }

    public static String buildUpdateWorkItemSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
                buildLogTableSql(rowTableName) +

                "UPDATE wi SET " +
                "full_code = LEFT(CAST(wi.full_code AS VARCHAR(4)), 2) " +
                "           + RIGHT('00' + CAST(? AS VARCHAR(2)), 2), " +
                "code=?, category_id=?, name=?, version=?, state=? " +

                buildOutputLogSql() + "INTO " + rowTableName + " " +

                "FROM work_items wi WHERE wi.work_item_id = ?; " +

                buildInsertLogSql(rowTableName, "UPDATE") +
                "SELECT work_item_id FROM " + rowTableName + ";";
            // buildLogTableSql(rowTableName) +
            // "UPDATE work_items SET full_code=?, code=?, category_id=?, name=?, version=?, state=? " +
            
            // buildOutputLogSql() + "INTO " + rowTableName + " " +
            // "WHERE work_item_id=?; " +

            // buildInsertLogSql(rowTableName, "UPDATE") +
            // "SELECT work_item_id FROM " + rowTableName + ";";
    }

    public static String buildDeleteWorkItemSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +
            "UPDATE work_items SET state = ? " +

            buildOutputLogSql() + "INTO " + rowTableName  + " " +
            "WHERE work_item_id = ? ; " +

            buildInsertLogSql(rowTableName, "DELETE") +
            "SELECT work_item_id FROM " + rowTableName + ";";
    }

    public static String buildDeleteWorkItemForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"

        return
            buildLogTableSql("@DeletedRows") +
            "UPDATE work_items SET state = ? " +

            buildOutputLogSql() + "INTO @DeletedRows " +
            "WHERE work_item_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLogSql("@DeletedRows", "DELETE") +
            "SELECT work_item_id FROM @DeletedRows;";
    }

    private static String baseSelectString() {
        return
            "SELECT w.work_item_id, w.full_code, w.code, w.category_id, wc.code as category_code, wc.name as category_name, w.name, w.version, w.state FROM work_items w" +
            " LEFT OUTER JOIN work_item_categories wc ON wc.work_item_category_id = w.category_id AND NOT (wc.state = ?)";
    }

    public static String buildFindByIdSql() {
        return 
            baseSelectString() + " WHERE w.work_item_id = ? AND NOT (w.state = ?)";
    }

    public static String buildFindAllSql() {
        return 
            baseSelectString() + " WHERE NOT (w.state = ?) ORDER BY w.full_code";
    }

    public static String buildFindAllByCategoryIdSql() {
        return 
            baseSelectString() + " WHERE w.category_id = ? AND NOT (w.state = ?) ORDER BY w.full_code";
    }

    public static String buildFindParentCategoryComboSql() {
        return "SELECT work_item_category_id as number, name as text FROM work_item_categories WHERE NOT (state = ?) ORDER BY code;";
    }

    public static String buildDownloadCsvWorkItemForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return 
            baseSelectString() + " WHERE w.work_item_id IN (" + placeholders + ") AND NOT (w.state = ?)";
    }
}
