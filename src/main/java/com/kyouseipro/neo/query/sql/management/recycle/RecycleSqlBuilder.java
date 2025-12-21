package com.kyouseipro.neo.query.sql.management.recycle;

import com.kyouseipro.neo.common.Utilities;

public class RecycleSqlBuilder {

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  recycle_id INT, recycle_number NVARCHAR(255), molding_number NVARCHAR(255), maker_id INT, item_id INT, use_date DATE, delivery_date DATE, shipping_date DATE, loss_date DATE," +
            "  company_id INT, office_id INT, recycling_fee INT, disposal_site_id INT, version INT, state INT" +
            "); ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO recycles_log (" +
            "  recycle_id, editor, process, log_date," +
            "  recycle_number, molding_number, maker_id, item_id, use_date, delivery_date, shipping_date, loss_date," +
            "  company_id, office_id, recycling_fee, disposal_site_id, version, state" +
            ") " +
            "SELECT recycle_id, ?, '" + processName + "', CURRENT_TIMESTAMP," +
            "  recycle_number, molding_number, maker_id, item_id, use_date, delivery_date, shipping_date, loss_date," +
            "  company_id, office_id, recycling_fee, disposal_site_id, version, state" +
            "  FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.recycle_id, INSERTED.recycle_number, INSERTED.molding_number, INSERTED.maker_id, INSERTED.item_id," +
            "  INSERTED.use_date, INSERTED.delivery_date, INSERTED.shipping_date, INSERTED.loss_date," +
            "  INSERTED.company_id, INSERTED.office_id, INSERTED.recycling_fee, INSERTED.disposal_site_id," +
            "  INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertRecycleSql(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "INSERT INTO recycles (" +
            " recycle_number, molding_number, maker_id, item_id, use_date, delivery_date, shipping_date, loss_date," +
            " company_id, office_id, recycling_fee, disposal_site_id, version, state" +
            ") " +

            buildOutputLogSql() + "INTO " + rowTableName + " " +

            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql(rowTableName, "INSERT") +

            "SELECT recycle_id FROM " + rowTableName + ";";
    }

    public static String buildUpdateRecycleSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "UPDATE recycles SET " +
            " recycle_number=?, molding_number=?, maker_id=?, item_id=?, use_date=?, delivery_date=?, shipping_date=?, loss_date=?," +
            " company_id=?, office_id=?, recycling_fee=?, disposal_site_id=?, version=?, state=? , update_date=?" +
            
            buildOutputLogSql() + "INTO " + rowTableName + " " +

            "WHERE recycle_id=?; " +

            buildInsertLogSql(rowTableName, "UPDATE") +

            "SELECT recycle_id FROM " + rowTableName + ";";
    }

    public static String buildUpdateRecycleDateSql(int index, String type) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "UPDATE recycles SET " + type + "_date=?, update_date=? " +

            buildOutputLogSql() + "INTO " + rowTableName  + " " +
            
            "WHERE recycle_id = ? ; " +

            buildInsertLogSql(rowTableName, "UPDATE") +

            "SELECT recycle_id FROM " + rowTableName + ";";
    }

    public static String buildInsertRecycleDateSql(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "INSERT INTO recycles (" +
            " recycle_number, molding_number, loss_date, update_date" +
            ") " +

            buildOutputLogSql() + "INTO " + rowTableName + " " +

            "VALUES (?, ?, ?, ?); " +

            buildInsertLogSql(rowTableName, "INSERT") +

            "SELECT recycle_id FROM " + rowTableName + ";";
    }

    public static String buildDeleteRecycleSql(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTableSql(rowTableName) +

            "UPDATE recycles SET state = ? " +

            buildOutputLogSql() + "INTO " + rowTableName  + " " +
            
            "WHERE recycle_id = ? ; " +

            buildInsertLogSql(rowTableName, "DELETE") +

            "SELECT recycle_id FROM " + rowTableName + ";";
    }

    public static String buildDeleteRecycleForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"

        return
            buildLogTableSql("@DeletedRows") +

            "UPDATE recycles SET state = ? " +

            buildOutputLogSql() + "INTO @DeletedRows " +

            "WHERE recycle_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLogSql("@DeletedRows", "DELETE") +

            "SELECT recycle_id FROM @DeletedRows;";
    }

    private static String baseSelectString() {
        return
            "SELECT r.recycle_id, r.recycle_number, r.molding_number, " +
            " r.maker_id, rm.code as maker_code, rm.name as maker_name, r.item_id, ri.code as item_code, ri.name as item_name," +
            " r.use_date, r.delivery_date, r.shipping_date, r.loss_date," +
            " r.company_id, r.office_id, c1.name as company_name, o.name as office_name," +
            " r.recycling_fee, r.disposal_site_id, c2.name as disposal_site_name, r.version, r.state" +
            " FROM recycles r" +
            " LEFT OUTER JOIN companies c1 ON c1.company_id = r.company_id AND NOT (c1.state = ?)" +
            " LEFT OUTER JOIN companies c2 ON c2.company_id = r.disposal_site_id AND NOT (c2.state = ?)" +
            " LEFT OUTER JOIN offices o ON o.office_id = r.office_id AND NOT (o.state = ?)" +
            " LEFT OUTER JOIN recycle_makers rm ON rm.recycle_maker_id = r.maker_id AND NOT (rm.state = ?)" +
            " LEFT OUTER JOIN recycle_items ri ON ri.recycle_item_id = r.item_id AND NOT (ri.state = ?)";
    }

    public static String buildFindByIdSql() {
        return 
            baseSelectString() + " WHERE r.recycle_id = ? AND NOT (r.state = ?)";
    }

    public static String buildFindByNumberSql() {
        return 
            baseSelectString() + " WHERE r.recycle_number = ? AND NOT (r.state = ?)";
    }
   
    public static String buildExistsByNumberSql() {
        return 
            baseSelectString() + " WHERE r.recycle_number = ? AND NOT (r.state = ?)";
    }

    // public static String buildExistsByDeliveryNumberSql() {
    //     return 
    //         "SELECT recycle_id as number, recycle_number as text FROM recycles WHERE recycle_number = ? AND NOT (state = ?) AND delivery_date != '9999-12-31'";
    // }

    // public static String buildExistsByShippingNumberSql() {
    //     return 
    //         "SELECT recycle_id as number, recycle_number as text FROM recycles WHERE recycle_number = ? AND NOT (state = ?) AND shipping_date != '9999-12-31'";
    // }

    // public static String buildExistsByLossNumberSql() {
    //     return 
    //         "SELECT recycle_id as number, recycle_number as text FROM recycles WHERE recycle_number = ? AND NOT (state = ?) AND loss_date != '9999-12-31'";
    // }

    public static String buildDownloadCsvRecycleForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count);
        return 
            baseSelectString() + " WHERE r.recycle_id IN (" + placeholders + ") AND NOT (r.state = ?)";
    }

    public static String buildFindByBetweenRecycleEntity(String col) {
        return 
            baseSelectString() +
            " WHERE NOT (r.state = ?) AND " +
            " r." + col + "_date >= ? AND r." + col + "_date < ?";

            // " WHERE NOT (r.state = ?) AND " +
            // "((r." + col + "_date >= ? AND r." + col + "_date <= ?) OR " +
            // "(r." + col + "_date >= ? AND r." + col + "_date <= '9999-12-31') OR " +
            // "(r." + col + "_date >= '9999-12-31' AND r." + col + "_date <= ?) OR " +
            // "(r." + col + "_date >= '9999-12-31' AND r." + col + "_date <= '9999-12-31'))";
    }

    // public static String buildFindByBetweenRecycleLossEntity() {
    //     return 
    //         baseSelectString() +
    //         " WHERE NOT (r.state = ?) AND " +
    //         " r.loss_date >= ? AND r.loss_date <= ?";

    //         // " WHERE NOT (r.state = ?) AND " +
    //         // "((r." + col + "_date >= ? AND r." + col + "_date <= ?) OR " +
    //         // "(r." + col + "_date >= ? AND r." + col + "_date <= '9999-12-31') OR " +
    //         // "(r." + col + "_date >= '9999-12-31' AND r." + col + "_date <= ?) OR " +
    //         // "(r." + col + "_date >= '9999-12-31' AND r." + col + "_date <= '9999-12-31'))";
    // }
}

