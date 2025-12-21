package com.kyouseipro.neo.query.sql.sales;

import com.kyouseipro.neo.common.Utilities;

public class WorkPriceSqlBuilder {
    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (work_price_id INT, work_item_id INT, company_id INT, price INT, version INT, state INT);";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO work_prices_log (work_price_id, editor, process, log_date, work_item_id, company_id, price, version, state) " +
            "SELECT work_price_id, ?, '" + processName + "', CURRENT_TIMESTAMP, work_item_id, company_id, price, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.work_price_id, INSERTED.work_item_id, INSERTED.company_id, INSERTED.price, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertWorkContentSql(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTableSql(rowTableName) +
            "INSERT INTO work_prices (work_item_id, company_id, price, version, state) " +

            buildOutputLogSql() + "INTO " + rowTableName + " " +
            "VALUES (?, ?, ?, ?, ?); " +

            buildInsertLogSql(rowTableName, "INSERT") +
            "SELECT work_price_id FROM " + rowTableName + ";";
    }

    public static String buildUpdateWorkContentSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +
            "UPDATE work_prices SET work_item_id=?, company_id=?, price=?, version=?, state=? " +
            
            buildOutputLogSql() + "INTO " + rowTableName + " " +
            "WHERE work_price_id=?; " +

            buildInsertLogSql(rowTableName, "UPDATE") +
            "SELECT work_price_id FROM " + rowTableName + ";";
    }

    public static String buildDeleteWorkContentSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +
            "UPDATE work_prices SET state = ? " +

            buildOutputLogSql() + "INTO " + rowTableName  + " " +
            "WHERE work_price_id = ? ; " +

            buildInsertLogSql(rowTableName, "DELETE") +
            "SELECT work_price_id FROM " + rowTableName + ";";
    }

    private static String baseSelectString() {
        return
            "SELECT w.work_price_id, w.work_item_id, w.company_id, w.price, w.version, w.state FROM work_prices w";
    }

    public static String buildFindByIdSql() {
        return 
            baseSelectString() + " WHERE w.work_price_id = ? AND NOT (w.state = ?)";
    }

    public static String buildFindAllSql() {
        return 
            baseSelectString() + " WHERE NOT (w.state = ?)";
    }

    public static String buildFindAllByCompanyIdSql() {
        return 
            baseSelectString() + " WHERE w.company_id = ? AND NOT (w.state = ?)";
    }

    public static String buildDownloadCsvWorkPriceForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return 
            baseSelectString() + " WHERE w.work_price_id IN (" + placeholders + ") AND NOT (w.state = ?)";
    }
}
