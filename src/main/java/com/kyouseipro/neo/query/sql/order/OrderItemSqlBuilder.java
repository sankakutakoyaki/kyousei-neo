package com.kyouseipro.neo.query.sql.order;

import com.kyouseipro.neo.common.Utilities;

public class OrderItemSqlBuilder {

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  order_item_id INT, order_id INT, company_id INT, office_id INT, delivery_address NVARCHAR(255), arrival_date DATE," +
            "  inspector_id INT, shipping_company_id INT, document_number NVARCHAR(255)," +
            "  item_maker NVARCHAR(255), item_name NVARCHAR(255), item_model NVARCHAR(255), item_quantity INT, item_payment INT," +
            "  buyer_id INT, remarks NVARCHAR(255), classification INT," +
            "  version INT, state INT" +
            "); ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO order_items_log (" +
            "  order_item_id, editor, process, log_date, order_id, company_id, office_id, delivery_address, arrival_date," +
            "  inspector_id, shipping_company_id, document_number," +
            "  item_maker, item_name, item_model, item_quantity, item_payment," +
            "  buyer_id, remarks, classification," +
            "  version, state" +
            ") " +
            "SELECT order_item_id, ?, '" + processName + "', CURRENT_TIMESTAMP, order_id, company_id, office_id, delivery_address, arrival_date," +
            "  inspector_id, shipping_company_id, document_number," +
            "  item_maker, item_name, item_model, item_quantity, item_payment," +
            "  buyer_id, remarks, classification," +
            "  version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.order_item_id, INSERTED.order_id, INSERTED.company_id, INSERTED.office_id, INSERTED.delivery_address, INSERTED.arrival_date," +
            "  INSERTED.inspector_id, INSERTED.shipping_company_id, INSERTED.document_number," +
            "  INSERTED.item_maker, INSERTED.item_name, INSERTED.item_model, INSERTED.item_quantity, INSERTED.item_payment," +
            "  INSERTED.buyer_id, INSERTED.remarks, INSERTED.classification," +
            "  INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertOrderItemSql(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "INSERT INTO order_items (" +
            " order_id, company_id, office_id, delivery_address, arrival_date, inspector_id, shipping_company_id, document_number," +
            " item_maker, item_name, item_model, item_quantity, item_payment, buyer_id, remarks, classification," +
            " version, state" +
            ") " +

            buildOutputLogSql() + "INTO " + rowTableName + " " +

            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql(rowTableName, "INSERT") +

            "SELECT order_item_id FROM " + rowTableName + ";";
    }

    public static String buildInsertOrderItemByNewOrderSql(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "INSERT INTO order_items (" +
            " order_id, company_id, office_id, delivery_address, arrival_date, inspector_id, shipping_company_id, document_number," +
            " item_maker, item_name, item_model, item_quantity, item_payment, buyer_id, remarks, classification," +
            " version, state" +
            ") " +

            buildOutputLogSql() + "INTO " + rowTableName + " " +

            "VALUES (@NEW_ID, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql(rowTableName, "INSERT") +

            "SELECT order_item_id FROM " + rowTableName + ";";
    }

    public static String buildUpdateOrderItemSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "UPDATE order_items SET " +
            "  order_id=?, company_id=?, office_id=?, delivery_address=?, arrival_date=?, inspector_id=?, shipping_company_id=?, document_number=?," +
            "  item_maker=?, item_name=?, item_model=?, item_quantity=?, item_payment=?, buyer_id=?, remarks=?, classification=?," +
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

    public static String buildDeleteOrderItemForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"

        return
            buildLogTableSql("@DeletedRows") +

            "UPDATE order_items SET state = ? " +

            buildOutputLogSql() + "INTO @DeletedRows " +
            
            "WHERE order_item_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLogSql("@DeletedRows", "DELETE") +

            "SELECT order_item_id FROM @DeletedRows;";
    }

    private static String baseSelectString() {
        return
            "SELECT oi.order_item_id, oi.order_id, oi.company_id, c1.name as company_name, oi.office_id, o.name as office_name, oi.delivery_address, oi.arrival_date," +
            " oi.inspector_id, e1.full_name as inspector_name, oi.shipping_company_id, c2.name as shipping_company_name, oi.document_number," +
            " oi.item_maker, oi.item_name, oi.item_model, oi.item_quantity, oi.item_payment," +
            " oi.buyer_id, e2.full_name as buyer_name, c3.name as buyer_company_name, oi.remarks, oi.classification," +
            " oi.version, oi.state FROM order_items oi" +
            " LEFT OUTER JOIN companies c1 ON c1.company_id = oi.company_id AND NOT (c1.state = ?)" +
            " LEFT OUTER JOIN companies c2 ON c2.company_id = oi.shipping_company_id AND NOT (c2.state = ?)" +
            " LEFT OUTER JOIN employees e1 ON e1.employee_id = oi.inspector_id AND NOT (e1.state = ?)" +
            " LEFT OUTER JOIN employees e2 ON e2.employee_id = oi.buyer_id AND NOT (e2.state = ?)" +
            " LEFT OUTER JOIN companies c3 ON c3.company_id = e2.company_id AND NOT (c3.state = ?)" +
            " LEFT OUTER JOIN offices o ON o.office_id = oi.office_id AND NOT (o.state = ?)";
    }

    public static String buildFindByIdSql() {
        return 
            baseSelectString() + " WHERE oi.order_item_id = ? AND NOT (oi.state = ?)";
    }

    // public static String buildFindAllSql() {
    //     return 
    //         baseSelectString() + " WHERE NOT (oi.state = ?)";
    // }

    public static String buildFindAllByOrderIdSql() {
        return 
            baseSelectString() + " WHERE oi.order_id = ? AND NOT (oi.state = ?)";
    }

    public static String buildDownloadCsvOrderItemForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count);
        return 
            baseSelectString() + " WHERE oi.order_item_id IN (" + placeholders + ") AND NOT (oi.state = ?)";
    }

    public static String buildFindByBetweenOrderItemEntity() {
        return 
            baseSelectString() +
            " WHERE NOT (oi.state = ?) AND " +
            "((oi.arrival_date >= ? AND oi.arrival_date <= ?) OR" +
            "(oi.arrival_date >= ? AND oi.arrival_date <= '9999-12-31') OR" +
            "(oi.arrival_date >= '9999-12-31' AND oi.arrival_date <= ?) OR" +
            "(oi.arrival_date >= '9999-12-31' AND oi.arrival_date <= '9999-12-31'))";
    }
}

