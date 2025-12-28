package com.kyouseipro.neo.query.sql.order;

import com.kyouseipro.neo.common.Utilities;

public class DeliveryStaffSqlBuilder {

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  delivery_staff_id INT, order_id INT, employee_id INT, version INT, state INT" +
            "); ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO delivery_staffs_log (" +
            "  delivery_staff_id, editor, process, log_date, order_id, employee_id, version, state" +
            ") " +
            "SELECT delivery_staff_id, ?, '" + processName + "', CURRENT_TIMESTAMP, order_id, employee_id, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.delivery_staff_id, INSERTED.order_id, INSERTED.employee_id, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertDeliveryStaffSql(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "INSERT INTO delivery_staffs (" +
            " order_id, employee_id, version, state" +
            ") " +

            buildOutputLogSql() + "INTO " + rowTableName+ " " +

            "VALUES (?, ?, ?, ?); " +

            buildInsertLogSql(rowTableName, "INSERT") +

            "SELECT delivery_staff_id FROM " + rowTableName + ";";
    }

    public static String buildInsertDeliveryStaffByNewOrderSql(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "INSERT INTO delivery_staffs (" +
            " order_id, employee_id, version, state" +
            ") " +

            buildOutputLogSql() + "INTO " + rowTableName+ " " +

            "VALUES (@NEW_ID, ?, ?, ?); " +

            buildInsertLogSql(rowTableName, "INSERT") +

            "SELECT delivery_staff_id FROM " + rowTableName + ";";
    }

    public static String buildUpdateDeliveryStaffSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "UPDATE delivery_staffs SET " +
            "  order_id=?, employee_id=?, version=?, state=? " +
            
            buildOutputLogSql() + "INTO " + rowTableName + " " +

            "WHERE delivery_staff_id=?; " +

            buildInsertLogSql(rowTableName, "UPDATE") +

            "SELECT delivery_staff_id FROM " + rowTableName + ";";
    }

    public static String buildDeleteDeliveryStaffSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "UPDATE delivery_staffs SET state = ? " +

            buildOutputLogSql() + "INTO " + rowTableName  + " " +
            
            "WHERE delivery_staff_id = ? ; " +

            buildInsertLogSql(rowTableName, "DELETE") +

            "SELECT delivery_staff_id FROM " + rowTableName + ";";
    }

    private static String baseSelectString() {
        return
            "SELECT ds.delivery_staff_id, ds.order_id, ds.employee_id, e.company_id, e.office_id" +
            ", COALESCE(e.full_name, '') as full_name, COALESCE(c.name, '') as company_name, COALESCE(o.name, '') as office_name" +
            ", ds.version, ds.state FROM delivery_staffs ds" +
            " LEFT OUTER JOIN employees e ON e.employee_id = ds.employee_id AND NOT (e.state = ?)" +
            " LEFT OUTER JOIN companies c ON e.company_id = c.company_id AND NOT (c.state = ?)" +
            " LEFT OUTER JOIN offices o ON e.office_id = o.office_id AND NOT (o.state = ?)";
    }
    public static String buildFindByIdSql() {
        return 
            baseSelectString() + " WHERE ds.delivery_staff_id = ? AND NOT (ds.state = ?)";
    }

    public static String buildFindAllSql() {
        return 
            baseSelectString() + " WHERE NOT (ds.state = ?)";
    }

    public static String buildFindAllByOrderIdSql() {
        return 
            baseSelectString() + " WHERE ds.order_id = ? AND NOT (ds.state = ?)";
    }

    public static String buildDownloadCsvOrderItemForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return 
            baseSelectString() + " WHERE ds.delivery_staff_id IN (" + placeholders + ") AND NOT (ds.state = ?)";
    }
}
