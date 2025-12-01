package com.kyouseipro.neo.query.sql.sales;

import com.kyouseipro.neo.common.Utilities;

public class WorkContentSqlBuilder {

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  work_content_id INT, order_id INT, work_content NVARCHAR(255), work_quantity INT, work_payment INT, " +
            "  version INT, state INT" +
            "); ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO work_contents_log (" +
            "  work_content_id, editor, process, log_date, order_id, work_content, work_quantity, work_payment, " +
            "  version, state" +
            ") " +
            "SELECT work_content_id, ?, '" + processName + "', CURRENT_TIMESTAMP, order_id, work_content, work_quantity, work_payment, " +
            "  version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.work_content_id, INSERTED.order_id, INSERTED.work_content, INSERTED.work_quantity, INSERTED.work_payment, " +
            "  INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertWorkContentSql(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "INSERT INTO work_contents (" +
            " order_id, work_content, work_quantity, work_payment, " +
            " version, state" +
            ") " +

            buildOutputLogSql() + "INTO " + rowTableName + " " +

            "VALUES (?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql(rowTableName, "INSERT") +

            "SELECT work_content_id FROM " + rowTableName + ";";
    }

    public static String buildInsertWorkContentByNewOrderSql(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "INSERT INTO work_contents (" +
            " order_id, work_content, work_quantity, work_payment, " +
            " version, state" +
            ") " +

            buildOutputLogSql() + "INTO " + rowTableName + " " +

            "VALUES (@NEW_ID, ?, ?, ?, ?, ?); " +

            buildInsertLogSql(rowTableName, "INSERT") +

            "SELECT work_content_id FROM " + rowTableName + ";";
    }

    public static String buildUpdateWorkContentSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "UPDATE work_contents SET " +
            "  order_id=?, work_content=?, work_quantity=?, work_payment=?, " +
            "  version=?, state=? " +
            
            buildOutputLogSql() + "INTO " + rowTableName + " " +

            "WHERE work_content_id=?; " +

            buildInsertLogSql(rowTableName, "UPDATE") +

            "SELECT work_content_id FROM " + rowTableName + ";";
    }

    public static String buildDeleteWorkContentSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "UPDATE work_contents SET state = ? " +

            buildOutputLogSql() + "INTO " + rowTableName  + " " +
            
            "WHERE work_content_id = ? ; " +

            buildInsertLogSql(rowTableName, "DELETE") +

            "SELECT work_content_id FROM " + rowTableName + ";";
    }

    private static String baseSelectString() {
        return
            "SELECT wc.work_content_id, wc.order_id, wc.work_content, wc.work_quantity, wc.work_payment," +
            " wc.version, wc.state FROM work_contents wc";
    }
    public static String buildFindByIdSql() {
        return 
            baseSelectString() + " WHERE wc.work_content_id = ? AND NOT (wc.state = ?)";
    }

    public static String buildFindAllSql() {
        return 
            baseSelectString() + " WHERE NOT (wc.state = ?)";
    }

    public static String buildFindAllByOrderIdSql() {
        return 
            baseSelectString() + " WHERE wc.order_id = ? AND NOT (wc.state = ?)";
    }

    public static String buildDownloadCsvWorkContentForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return 
            baseSelectString() + " WHERE wc.work_content_id IN (" + placeholders + ") AND NOT (wc.state = ?)";
    }
}

