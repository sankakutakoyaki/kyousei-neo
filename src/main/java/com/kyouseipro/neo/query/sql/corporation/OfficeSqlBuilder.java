package com.kyouseipro.neo.query.sql.corporation;

import com.kyouseipro.neo.common.Utilities;

public class OfficeSqlBuilder {

    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  office_id INT, company_id INT, name NVARCHAR(255), name_kana NVARCHAR(255), " +
            "  tel_number NVARCHAR(255), fax_number NVARCHAR(255), postal_code NVARCHAR(255), " +
            "  full_address NVARCHAR(255), email NVARCHAR(255), web_address NVARCHAR(255), " +
            "  version INT, state INT" +
            "); ";
    }

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.office_id, INSERTED.company_id, INSERTED.name, INSERTED.name_kana, " +
            "INSERTED.tel_number, INSERTED.fax_number, INSERTED.postal_code, INSERTED.full_address, " +
            "INSERTED.email, INSERTED.web_address, INSERTED.version, INSERTED.state ";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
        return
            "INSERT INTO offices_log (" +
            "  office_id, editor, process, log_date, company_id, name, name_kana, tel_number, fax_number, " +
            "  postal_code, full_address, email, web_address, version, state" +
            ") " +
            "SELECT office_id, ?, '" + processName + "', CURRENT_TIMESTAMP, " +
            "  company_id, name, name_kana, tel_number, fax_number, " +
            "  postal_code, full_address, email, web_address, version, state " +
            "FROM " + rowTableName + ";";
    }

    public static String buildInsert() {
        return
            buildLogTable("@Inserted") +

            "INSERT INTO offices (" +
            "  company_id, name, name_kana, tel_number, fax_number, " +
            "  postal_code, full_address, email, web_address, version, state" +
            ") " +
            buildOutputLog() + "INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLog("@Inserted", "INSERT") +
            "SELECT office_id FROM @Inserted;";
    }

    public static String buildUpdate() {
        return
            buildLogTable("@Updated") +

            "UPDATE offices SET " +
            "  company_id=?, name=?, name_kana=?, tel_number=?, fax_number=?, " +
            "  postal_code=?, full_address=?, email=?, web_address=?, version=?, state=? " +
            buildOutputLog() + "INTO @Updated " +
            "WHERE office_id=? AND version=?; " +

            buildInsertLog("@Updated", "UPDATE") +
            "SELECT office_id FROM @Updated;";
    }

    public static String buildDeleteByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count);

        return
            buildLogTable("@Deleted") +

            "UPDATE offices SET state = ? " +
            buildOutputLog() + "INTO @Deleted " +
            "WHERE office_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLog("@Deleted", "DELETE") +
            "SELECT office_id FROM @Deleted;";
    }

    private static String baseSelectString() {
        return
            "SELECT o.*, c.name as company_name, c.name_kana as company_name_kana FROM offices o" + 
            " INNER JOIN companies c ON c.company_id = o.company_id AND NOT (c.state = ?)";
    }

    public static String buildFindById() {
        return
            baseSelectString() +
            " WHERE NOT (o.state = ?) AND o.office_id = ?";
    }

    public static String buildFindAll() {
        return
            baseSelectString() +
            " WHERE NOT (o.state = ?)";
    }

    public static String buildFindAllClient() {
        return 
            baseSelectString() + 
            " WHERE NOT (o.state = ?) AND NOT (c.category = ?)";
    }

    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return
            baseSelectString() +
            " WHERE o.office_id IN (" + placeholders + ") AND NOT (o.state = ?)";
    }
}
