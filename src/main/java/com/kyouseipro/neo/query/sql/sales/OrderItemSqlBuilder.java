package com.kyouseipro.neo.query.sql.sales;

import com.kyouseipro.neo.common.Utilities;

public class OrderItemSqlBuilder {

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  order_item_id INT, order_id INT, item_maker NVARCHAR(255), item_name NVARCHAR(255), item_model NVARCHAR(255), item_quantity INT, arrival_date DATE, " +
            "  version INT, state INT" +
            "); ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO order_items_log (" +
            "  order_item_id, editor, process, log_date, order_id, item_maker, item_name, item_model, item_quantity, arrival_date, " +
            "  version, state" +
            ") " +
            "SELECT order_item_id, ?, '" + processName + "', CURRENT_TIMESTAMP, order_id, item_maker, item_name, item_model, item_quantity, arrival_date, " +
            "  version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.order_item_id, INSERTED.order_id, INSERTED.item_maker, INSERTED.item_name, INSERTED.item_model, INSERTED.item_quantity, INSERTED.arrival_date, " +
            "  INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertOrderItemSql(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "INSERT INTO order_items (" +
            " order_id, item_maker, item_name, item_model, item_quantity, arrival_date, " +
            " version, state" +
            ") " +

            buildOutputLogSql() + "INTO " + rowTableName + " " +

            "VALUES (?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql(rowTableName, "INSERT") +

            "SELECT order_item_id FROM " + rowTableName + ";";
    }

    public static String buildInsertNewOrderItemSql(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "INSERT INTO order_items (" +
            " order_id, item_maker, item_name, item_model, item_quantity, arrival_date, " +
            " version, state" +
            ") " +

            buildOutputLogSql() + "INTO " + rowTableName + " " +

            "VALUES (@NEW_ID, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql(rowTableName, "INSERT") +

            "SELECT order_item_id FROM " + rowTableName + ";";
    }

    public static String buildUpdateOrderItemSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "UPDATE order_items SET " +
            "  order_id=?, item_maker=?, item_name=?, item_model=?, item_quantity=?, arrival_date=?, " +
            "  version=?, state=? " +
            
            buildOutputLogSql() + "INTO " + rowTableName + " " +

            "WHERE order_item_id=?; " +

            buildInsertLogSql(rowTableName, "UPDATE") +

            "SELECT order_item_id FROM " + rowTableName + ";";
    }

    public static String buildDeleteOrderItemSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "UPDATE order_items SET state = ? " +

            buildOutputLogSql() + "INTO " + rowTableName  + " " +
            
            "WHERE order_item_id = ? ; " +

            buildInsertLogSql(rowTableName, "DELETE") +

            "SELECT order_item_id FROM " + rowTableName + ";";
    }

    // public static String buildDeleteOrderItemForIdsSql(int count) {
    //     String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"

    //     return
    //         buildLogTableSql("@DeletedRows") +

    //         "UPDATE order_items SET state = ? " +

    //         buildOutputLogSql() + "INTO @DeletedRows " +
            
    //         "WHERE order_item_id IN (" + placeholders + ") AND NOT (state = ?); " +

    //         buildInsertLogSql("@DeletedRows", "DELETE") +

    //         "SELECT order_item_id FROM @DeletedRows;";
    // }

    private static String baseSelectString() {
        return
            "SELECT oi.order_item_id, oi.order_id, oi.item_maker, oi.item_name, oi.item_model, oi.item_quantity, oi.arrival_date," +
            " oi.version, oi.state FROM order_items oi";
    }
    public static String buildFindByIdSql() {
        return 
            baseSelectString() + " WHERE oi.order_item_id = ? AND NOT (oi.state = ?)";
    }

    public static String buildFindAllSql() {
        return 
            baseSelectString() + " WHERE NOT (oi.state = ?)";
    }

    public static String buildFindAllByOrderIdSql() {
        return 
            baseSelectString() + " WHERE oi.order_id = ? AND NOT (oi.state = ?)";
    }

    public static String buildDownloadCsvOrderItemForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return 
            baseSelectString() + " WHERE oi.order_item_id IN (" + placeholders + ") AND NOT (oi.state = ?)";
    }
}

