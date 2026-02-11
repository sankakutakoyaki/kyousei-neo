package com.kyouseipro.neo.recycle.regist.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.recycle.regist.entity.RecycleEntityRequest;

public class RecycleSqlBuilder {

    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  recycle_id INT, recycle_number NVARCHAR(255), molding_number NVARCHAR(255), maker_id INT, item_id INT, use_date DATE, delivery_date DATE, shipping_date DATE, loss_date DATE," +
            "  company_id INT, office_id INT, recycling_fee INT, disposal_site_id INT, slip_number INT, version INT, state INT" +
            "); ";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
        return
            "INSERT INTO recycles_log (" +
            "  recycle_id, editor, process, log_date," +
            "  recycle_number, molding_number, maker_id, item_id, use_date, delivery_date, shipping_date, loss_date," +
            "  company_id, office_id, recycling_fee, disposal_site_id, slip_number, version, state" +
            ") " +
            "SELECT recycle_id, ?, '" + processName + "', CURRENT_TIMESTAMP," +
            "  recycle_number, molding_number, maker_id, item_id, use_date, delivery_date, shipping_date, loss_date," +
            "  company_id, office_id, recycling_fee, disposal_site_id, slip_number, version, state" +
            "  FROM " + rowTableName + ";";
    }

    private static String buildOutputLog() {
        // return "OUTPUT INSERTED.* ";
        return
            " OUTPUT INSERTED.recycle_id, INSERTED.recycle_number, INSERTED.molding_number, INSERTED.maker_id, INSERTED.item_id," +
            "  INSERTED.use_date, INSERTED.delivery_date, INSERTED.shipping_date, INSERTED.loss_date," +
            "  INSERTED.company_id, INSERTED.office_id, INSERTED.recycling_fee, INSERTED.disposal_site_id, INSERTED.slip_number," +
            "  INSERTED.version, INSERTED.state ";
    }

    public static String buildInsert(int index) {
        String rowTableName = "@InsertedRows" + index;
        return
            buildLogTable(rowTableName) +

            "INSERT INTO recycles (" +
            " recycle_number, molding_number, maker_id, item_id, use_date, delivery_date, shipping_date, loss_date," +
            " company_id, office_id, recycling_fee, disposal_site_id, slip_number, version, state" +
            ") " +

            buildOutputLog() + "INTO " + rowTableName + " " +

            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLog(rowTableName, "INSERT") +

            "SELECT recycle_id FROM " + rowTableName + ";";
    }

    public static String buildUpdate(int index, String type) {
        String rowTableName = "@UpdatedRows" + index;
        String sql = "UPDATE recycles SET ";
        String fil = ";";

        switch (type) {
            case "edit":
                sql += """
                     recycle_number=?, molding_number=?, maker_id=?, item_id=?, use_date=?, delivery_date=?, shipping_date=?, loss_date=?,
                     company_id=?, office_id=?, recycling_fee=?, disposal_site_id=?, slip_number=?, version=?, state=? , update_date=? """;
                break;
            case "delivery":
                sql += """
                     delivery_date=?, disposal_site_id=?, version=?, state=?, update_date=? """;
                break;
            case "shipping":
                sql += """
                     shipping_date=?, version=?, state=? , update_date=? """;
                fil = """
                     AND delivery_date IS NOT NULL; """;
                break;
            case "loss":
                sql += """
                     loss_date=?, version=?, state=? , update_date=? """;
                break;
            default:
                break;
        }
        return
            buildLogTable(rowTableName) + sql +
            
            buildOutputLog() + "INTO " + rowTableName + " " +

            // "WHERE recycle_id=? AND version=?; " +
            "WHERE recycle_number=? AND NOT (state=?) " + fil +

            buildInsertLog(rowTableName, "UPDATE") +

            "SELECT recycle_id FROM " + rowTableName + ";";
    }

    public static String buildBulkUpdate(RecycleEntityRequest req) {

        if (req.getRecycleNumbers() == null || req.getRecycleNumbers().isEmpty()) {
            throw new IllegalArgumentException("recycleNumbers is required");
        }

        List<String> sets = new ArrayList<>();

        if (req.getUseDate() != null) {
            sets.add("use_date=?");
        }
        if (req.getDeliveryDate() != null) {
            sets.add("delivery_date=?");
        }
        if (req.getShippingDate() != null) {
            sets.add("shipping_date=?");
        }
        if (req.getLossDate() != null) {
            sets.add("loss_date=?");
        }
        if (req.getMakerId() != null) {
            sets.add("maker_id=?");
        }
        if (req.getItemId() != null) {
            sets.add("item_id=?");
        }
        if (req.getCompanyId() != null) {
            sets.add("company_id=?");
        }
        if (req.getOfficeId() != null) {
            sets.add("office_id=?");
        }
        if (req.getRecyclingFee() != null) {
            sets.add("recycling_fee=?");
        }
        if (req.getDisposalSiteId() != null) {
            sets.add("disposal_site_id=?");
        }
        if (req.getSlipNumber() != null) {
            sets.add("slip_number=?");
        }
        if (req.getState() != null) {
            sets.add("state=?");
        }

        // 共通更新
        sets.add("version = version + 1");
        sets.add("update_date = SYSDATETIME()");

        if (sets.isEmpty()) {
            throw new IllegalArgumentException("No update fields");
        }

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE recycles SET ");
        sql.append(String.join(", ", sets));
        sql.append(" WHERE recycle_number IN (");
        sql.append(req.getRecycleNumbers().stream().map(v -> "?").collect(Collectors.joining(",")));
        sql.append(")");

        return 
            sql.toString();
    }

    public static String buildUpdateForDate(int index, String type) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTable(rowTableName) +

            "UPDATE recycles SET " + type + "_date=?, update_date=? " +

            buildOutputLog() + "INTO " + rowTableName  + " " +
            
            "WHERE recycle_number = ? ; " +

            buildInsertLog(rowTableName, "UPDATE") +

            "SELECT recycle_id FROM " + rowTableName + ";";
    }

    public static String buildDelete(int index) {
        String rowTableName = "@UpdatedRows" + index;
        return
            buildLogTable(rowTableName) +

            "UPDATE recycles SET state = ? " +

            buildOutputLog() + "INTO " + rowTableName  + " " +
            
            "WHERE recycle_id = ? ; " +

            buildInsertLog(rowTableName, "DELETE") +

            "SELECT recycle_id FROM " + rowTableName + ";";
    }

    public static String buildDeleteByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"

        return
            buildLogTable("@DeletedRows") +

            "UPDATE recycles SET state = ? " +

            buildOutputLog() + "INTO @DeletedRows " +

            "WHERE recycle_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLog("@DeletedRows", "DELETE") +

            "SELECT recycle_id FROM @DeletedRows;";
    }

    private static String baseSelectString() {
        return
            "SELECT r.recycle_id, r.recycle_number, r.molding_number, " +
            " r.maker_id, rm.code as maker_code, rm.name as maker_name, r.item_id, ri.code as item_code, ri.name as item_name," +
            " r.use_date, r.delivery_date, r.shipping_date, r.loss_date," +
            " r.company_id, r.office_id, c1.name as company_name, o.name as office_name," +
            " r.recycling_fee, r.disposal_site_id, c2.name as disposal_site_name, r.slip_number, r.version, r.state, r.regist_date, r.update_date" +
            " FROM recycles r" +
            " LEFT OUTER JOIN companies c1 ON c1.company_id = r.company_id AND NOT (c1.state = ?)" +
            " LEFT OUTER JOIN companies c2 ON c2.company_id = r.disposal_site_id AND NOT (c2.state = ?)" +
            " LEFT OUTER JOIN offices o ON o.office_id = r.office_id AND NOT (o.state = ?)" +
            " LEFT OUTER JOIN recycle_makers rm ON rm.recycle_maker_id = r.maker_id AND NOT (rm.state = ?)" +
            " LEFT OUTER JOIN recycle_items ri ON ri.recycle_item_id = r.item_id AND NOT (ri.state = ?)";
    }

    public static String buildFindById() {
        return 
            baseSelectString() + " WHERE r.recycle_id = ? AND NOT (r.state = ?)";
    }

    public static String buildFindByNumber() {
        return 
            baseSelectString() + " WHERE r.recycle_number = ? AND NOT (r.state = ?)";
    }
   
    public static String buildExistsByNumber() {
        return 
            baseSelectString() + " WHERE r.recycle_number = ? AND NOT (r.state = ?)";
    }

    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count);
        return 
            baseSelectString() + " WHERE r.recycle_id IN (" + placeholders + ") AND NOT (r.state = ?)";
    }

    public static String buildFindByBetween(String col) {
        return 
            baseSelectString() +
            " WHERE NOT (r.state = ?) AND " +
            " r." + col + "_date >= ? AND r." + col + "_date < ?";
    }
}

