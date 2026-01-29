package com.kyouseipro.neo.query.sql.corporation;

import com.kyouseipro.neo.common.Utilities;

public class CompanySqlBuilder {

    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  company_id INT, category NVARCHAR(255), name NVARCHAR(255), name_kana NVARCHAR(255), " +
            "  tel_number NVARCHAR(255), fax_number NVARCHAR(255), postal_code NVARCHAR(255), " +
            "  full_address NVARCHAR(255), email NVARCHAR(255), web_address NVARCHAR(255), is_original_price INT, " +
            "  version INT, state INT" +
            "); ";
    }

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.company_id, INSERTED.category, INSERTED.name, INSERTED.name_kana, " +
            "INSERTED.tel_number, INSERTED.fax_number, INSERTED.postal_code, INSERTED.full_address, " +
            "INSERTED.email, INSERTED.web_address, INSERTED.is_original_price, INSERTED.version, INSERTED.state ";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
        return
            "INSERT INTO companies_log (" +
            "  company_id, editor, process, log_date, category, name, name_kana, tel_number, fax_number, " +
            "  postal_code, full_address, email, web_address, is_original_price, version, state" +
            ") " +
            "SELECT company_id, ?, '" + processName + "', CURRENT_TIMESTAMP, category, name, name_kana, tel_number, fax_number, " +
            "  postal_code, full_address, email, web_address, is_original_price, version, state " +
            "FROM " + rowTableName + ";";
    }

    public static String buildInsert() {
        return
            buildLogTable("@Inserted") +

            "INSERT INTO companies (" +
            "  category, name, name_kana, tel_number, fax_number, postal_code, full_address, " +
            "  email, web_address, is_original_price, version, state" +
            ") " +
            buildOutputLog() + "INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLog("@Inserted", "INSERT") +
            "SELECT company_id FROM @Inserted;";
    }

    public static String buildUpdate() {
        return
            buildLogTable("@Updated") +

            "UPDATE companies SET " +
            "  category=?, name=?, name_kana=?, tel_number=?, fax_number=?, postal_code=?, " +
            "  full_address=?, email=?, web_address=?, is_original_price=?, version=?, state=? " +
            buildOutputLog() + "INTO @Updated " +
            "WHERE company_id=? AND version=?; " +

            buildInsertLog("@Updated", "UPDATE") +
            "SELECT company_id FROM @Updated;";
    }

    public static String buildDeleteByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count);

        return
            buildLogTable("@Deleted") +

            "UPDATE companies SET state = ? " +
            buildOutputLog() + "INTO @Deleted " +
            "WHERE company_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLog("@Deleted", "DELETE") +
            "SELECT company_id FROM @Deleted;";
    }

    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return "SELECT * FROM companies WHERE company_id IN (" + placeholders + ") AND NOT (state = ?)";
    }
}
