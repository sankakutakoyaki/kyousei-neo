package com.kyouseipro.neo.query.sql.sales;

import com.kyouseipro.neo.common.Utilities;

public class OrderSqlBuilder {

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  order_id INT, request_number NVARCHAR(255), order_date DATE, start_date DATE, end_date DATE, " +
            "  prime_constractor_id INT, prime_constractor_office_id INT, " +
            "  title NVARCHAR(255), order_postal_code NVARCHAR(255), order_full_address NVARCHAR(255), " +
            "  version INT, state INT" +
            "); ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO orders_log (" +
            "  order_id, editor, process, log_date, request_number, order_date, start_date, end_date, " +
            "  prime_constractor_id, prime_constractor_office_id, title, order_postal_code, order_full_address, " +
            "  version, state" +
            ") " +
            "SELECT order_id, ?, '" + processName + "', CURRENT_TIMESTAMP, request_number, order_date, start_date, end_date, " +
            "  prime_constractor_id, prime_constractor_office_id, title, order_postal_code, order_full_address, " +
            "  version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.order_id, INSERTED.request_number, INSERTED.order_date, INSERTED.start_date, INSERTED.end_date, " +
            "  INSERTED.prime_constractor_id, INSERTED.prime_constractor_office_id, INSERTED.title, INSERTED.order_postal_code, INSERTED.order_full_address, " +
            "  INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertOrderSql() {
        return
            buildLogTableSql("@InsertedRows") +

            "INSERT INTO orders (" +
            "  request_number, order_date, start_date, end_date, " +
            "  title, order_postal_code, order_full_address, " +
            "  version, state" +
            ") " +

            buildOutputLogSql() + "INTO @InsertedRows " +

            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql("@InsertedRows", "INSERT") +

            "SELECT order_id FROM @InsertedRows;";
    }

    public static String buildUpdateOrderSql() {
        return
            buildLogTableSql("@UpdatedRows") +

            "UPDATE orders SET " +
            "  request_number=?, order_date=?, start_date=?, end_date=?, " +
            "  title=?, order_postal_code=?, order_full_address=?, " +
            "  version=?, state=? " +
            
            buildOutputLogSql() + "INTO @UpdatedRows " +

            "WHERE order_id=?; " +

            buildInsertLogSql("@UpdatedRows", "UPDATE") +

            "SELECT order_id FROM @UpdatedRows;";
    }

    public static String buildDeleteOrderForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"

        return
            buildLogTableSql("@DeletedRows") +

            "UPDATE orders SET state = ? " +

            buildOutputLogSql() + "INTO @DeletedRows " +
            
            "WHERE order_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLogSql("@DeletedRows", "DELETE") +

            "SELECT order_id FROM @DeletedRows;";
    }

    public static String buildFindByIdSql() {
        return "SELECT * FROM orders WHERE order_id = ? AND NOT (state = ?)";
    }

    public static String buildFindAllSql() {
        return "SELECT * FROM orders WHERE NOT (state = ?)";
    }

    public static String buildDownloadCsvOrderForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return "SELECT * FROM orders WHERE order_id IN (" + placeholders + ") AND NOT (state = ?)";
    }
}
