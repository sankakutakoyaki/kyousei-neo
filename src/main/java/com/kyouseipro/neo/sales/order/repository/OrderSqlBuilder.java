package com.kyouseipro.neo.sales.order.repository;

import com.kyouseipro.neo.common.Utilities;

public class OrderSqlBuilder {

    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  order_id INT, request_number NVARCHAR(255), start_date DATE, end_date DATE, " +
            "  prime_constractor_id INT, prime_constractor_office_id INT, " +
            "  title NVARCHAR(255), order_postal_code NVARCHAR(255), order_full_address NVARCHAR(255), " +
            "  contact_information NVARCHAR(255), contact_information2 NVARCHAR(255), remarks NVARCHAR(255), " +
            "  version INT, state INT" +
            "); ";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
        return
            "INSERT INTO orders_log (" +
            "  order_id, editor, process, log_date, request_number, start_date, end_date, " +
            "  prime_constractor_id, prime_constractor_office_id, title, order_postal_code, order_full_address, " +
            "  contact_information, contact_information2, remarks, " +
            "  version, state" +
            ") " +
            "SELECT order_id, ?, '" + processName + "', CURRENT_TIMESTAMP, request_number, start_date, end_date, " +
            "  prime_constractor_id, prime_constractor_office_id, title, order_postal_code, order_full_address, " +
            "  contact_information, contact_information2, remarks, " +
            "  version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.order_id, INSERTED.request_number, INSERTED.start_date, INSERTED.end_date, " +
            "  INSERTED.prime_constractor_id, INSERTED.prime_constractor_office_id, INSERTED.title, " +
            "  INSERTED.order_postal_code, INSERTED.order_full_address, " +
            "  INSERTED.contact_information, INSERTED.contact_information2, INSERTED.remarks, " +
            "  INSERTED.version, INSERTED.state ";
    }

    public static String buildInsert() {
        return
            buildLogTable("@InsertedRows") +

            "INSERT INTO orders (" +
            " request_number, start_date, end_date, " +
            " prime_constractor_id, prime_constractor_office_id, " +
            " title, order_postal_code, order_full_address, contact_information, contact_information2, remarks, " +
            " version, state" +
            ") " +

            buildOutputLog() + "INTO @InsertedRows " +

            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLog("@InsertedRows", "INSERT") +

            "SELECT order_id FROM @InsertedRows;" +
            // "DECLARE @NEW_ID int; SET @NEW_ID = @@IDENTITY;";
            "DECLARE @NEW_ID int; SELECT @NEW_ID = order_id FROM @InsertedRows;";
    }

    public static String buildUpdate() {
        return
            buildLogTable("@UpdatedRows") +

            // "DECLARE @NEW_ID int; SET @NEW_ID = ?;" +
            "UPDATE orders SET " +
            "  request_number=?, start_date=?, end_date=?, " +
            "  prime_constractor_id=?, prime_constractor_office_id=?, " +
            "  title=?, order_postal_code=?, order_full_address=?, contact_information=?, contact_information2=?, remarks=?, " +
            "  version=?, state=? " +
            
            buildOutputLog() + "INTO @UpdatedRows " +

            "WHERE order_id=? AND version=?; " +

            buildInsertLog("@UpdatedRows", "UPDATE") +

            "SELECT order_id FROM @UpdatedRows;";
            
    }

    public static String buildDeleteByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"

        return
            buildLogTable("@DeletedRows") +

            "UPDATE orders SET state = ? " +

            buildOutputLog() + "INTO @DeletedRows " +
            
            "WHERE order_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLog("@DeletedRows", "DELETE") +

            "SELECT order_id FROM @DeletedRows;";
    }

    private static String baseSelectString() {
        return
            "SELECT ord.order_id, ord.request_number, ord.start_date, ord.end_date" +
            ", ord.prime_constractor_id, ord.prime_constractor_office_id" +
            ", COALESCE(c.name, '') as prime_constractor_name, COALESCE(o.name, '') as prime_constractor_office_name" +
            ", ord.title, ord.order_postal_code, ord.order_full_address" +
            ", ord.contact_information, ord.contact_information2, ord.remarks, ord.version, ord.state FROM orders ord" +
            " LEFT OUTER JOIN companies c ON c.company_id = ord.prime_constractor_id AND NOT (c.state = ?)" +
            " LEFT OUTER JOIN offices o ON o.office_id = ord.prime_constractor_office_id AND NOT (o.state = ?)";
    }
    public static String buildFindById() {
        return 
            baseSelectString() + " WHERE ord.order_id = ? AND NOT (ord.state = ?)";
    }

    public static String buildFindAll() {
        return 
            baseSelectString() + " WHERE NOT (ord.state = ?)";
    }

    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return 
            baseSelectString() + " WHERE ord.order_id IN (" + placeholders + ") AND NOT (ord.state = ?)";
    }
}
