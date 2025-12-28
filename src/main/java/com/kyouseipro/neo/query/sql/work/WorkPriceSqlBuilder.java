package com.kyouseipro.neo.query.sql.work;

import com.kyouseipro.neo.common.Utilities;

public class WorkPriceSqlBuilder {
    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (work_price_id INT, work_item_id INT, company_id INT, price INT, version INT, state INT);";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
        return
            "INSERT INTO work_prices_log (work_price_id, editor, process, log_date, work_item_id, company_id, price, version, state) " +
            "SELECT work_price_id, ?, '" + processName + "', CURRENT_TIMESTAMP, work_item_id, company_id, price, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.work_price_id, INSERTED.work_item_id, INSERTED.company_id, INSERTED.price, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsert(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTable(rowTableName) +
            "INSERT INTO work_prices (work_item_id, company_id, price, version, state) " +

            buildOutputLog() + "INTO " + rowTableName + " " +
            "VALUES (?, ?, ?, ?, ?); " +

            buildInsertLog(rowTableName, "INSERT") +
            "SELECT work_price_id FROM " + rowTableName + ";";
    }

    public static String buildUpdate(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTable(rowTableName) +
            "UPDATE work_prices SET work_item_id=?, company_id=?, price=?, version=?, state=? " +
            
            buildOutputLog() + "INTO " + rowTableName + " " +
            "WHERE work_price_id=?; " +

            buildInsertLog(rowTableName, "UPDATE") +
            "SELECT work_price_id FROM " + rowTableName + ";";
    }

    public static String buildDelete(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTable(rowTableName) +
            "UPDATE work_prices SET state = ? " +

            buildOutputLog() + "INTO " + rowTableName  + " " +
            "WHERE work_price_id = ? ; " +

            buildInsertLog(rowTableName, "DELETE") +
            "SELECT work_price_id FROM " + rowTableName + ";";
    }

    public static String buildDeleteByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"

        return
            buildLogTable("@DeletedRows") +
            "UPDATE work_prices SET state = ? " +

            buildOutputLog() + "INTO @DeletedRows " +
            "WHERE work_price_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLog("@DeletedRows", "DELETE") +
            "SELECT work_price_id FROM @DeletedRows;";
    }

    private static String baseSelectString() {
        return
            "SELECT wi.work_item_id, wi.full_code, wi.name as work_item_name, wi.version, wi.state, " +
            "wp.price, wp.work_price_id, wp.company_id as company_id, c.name as company_name, " +
            "wi.category_id, wc.name as category_name FROM work_items wi " +
            "LEFT JOIN work_prices wp ON wp.work_item_id = wi.work_item_id AND NOT (wp.state = ?) " +
            "LEFT JOIN companies c ON c.company_id = wp.company_id AND NOT (c.state = ?) " +
            "LEFT JOIN work_item_categories wc ON wc.work_item_category_id = wi.category_id AND NOT (wc.state = ?) ";
    }
    
    public static String buildFindById() {
        return 
            baseSelectString() + " WHERE w.work_price_id = ? AND NOT (w.state = ?)";
    }

    public static String buildFindAllByCompanyId() {
        return
            "SELECT wi.work_item_id, wi.full_code, wi.name as work_item_name, wi.version, wi.state, " +
            "wp.price, wp.work_price_id, COALESCE(NULLIF(wp.company_id, 0), ?) AS company_id, c.name as company_name, " +
            "wi.category_id, wc.name as category_name FROM work_items wi " +
            "LEFT JOIN work_prices wp ON wp.work_item_id = wi.work_item_id AND wp.company_id = ? AND NOT (wp.state = ?) " +
            "LEFT JOIN companies c ON c.company_id = COALESCE(NULLIF(wp.company_id, 0), ?) AND NOT (c.state = ?) " +
            "LEFT JOIN work_item_categories wc ON wc.work_item_category_id = wi.category_id AND NOT (wc.state = ?) " +
            "WHERE NOT(wi.state = ?) ORDER BY wi.category_id, wi.code;";
    }

    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return
            baseSelectString() + "WHERE w.work_price_id IN (" + placeholders + ") AND NOT (w.state = ?)";
    }
}
