package com.kyouseipro.neo.query.sql.corporation;

import com.kyouseipro.neo.common.Utilities;

public class OfficeSqlBuilder {

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  office_id INT, company_id INT, name NVARCHAR(255), name_kana NVARCHAR(255), " +
            "  tel_number NVARCHAR(255), fax_number NVARCHAR(255), postal_code NVARCHAR(255), " +
            "  full_address NVARCHAR(255), email NVARCHAR(255), web_address NVARCHAR(255), " +
            "  version INT, state INT" +
            "); ";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.office_id, INSERTED.company_id, INSERTED.name, INSERTED.name_kana, " +
            "INSERTED.tel_number, INSERTED.fax_number, INSERTED.postal_code, INSERTED.full_address, " +
            "INSERTED.email, INSERTED.web_address, INSERTED.version, INSERTED.state ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
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

    public static String buildInsertOfficeSql() {
        return
            buildLogTableSql("@Inserted") +

            "INSERT INTO offices (" +
            "  company_id, name, name_kana, tel_number, fax_number, " +
            "  postal_code, full_address, email, web_address, version, state" +
            ") " +
            buildOutputLogSql() + "INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql("@Inserted", "INSERT") +
            "SELECT office_id FROM @Inserted;";
    }

    public static String buildUpdateOfficeSql() {
        return
            buildLogTableSql("@Updated") +

            "UPDATE offices SET " +
            "  company_id=?, name=?, name_kana=?, tel_number=?, fax_number=?, " +
            "  postal_code=?, full_address=?, email=?, web_address=?, version=?, state=? " +
            buildOutputLogSql() + "INTO @Updated " +
            "WHERE office_id=?; " +

            buildInsertLogSql("@Updated", "UPDATE") +
            "SELECT office_id FROM @Updated;";
    }

    public static String buildDeleteOfficeForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count);

        return
            buildLogTableSql("@Deleted") +

            "UPDATE offices SET state = ? " +
            buildOutputLogSql() + "INTO @Deleted " +
            "WHERE office_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLogSql("@Deleted", "DELETE") +
            "SELECT office_id FROM @Deleted;";
    }

    private static String baseSelectString() {
        return
            "SELECT o.*, c.name as company_name, c.name_kana as company_name_kana FROM offices o" + 
            " INNER JOIN companies c ON c.company_id = o.company_id";
    }

    public static String buildFindByIdSql() {
        return
            baseSelectString() +
            " WHERE o.office_id = ? AND NOT (o.state = ?)";
    }

    public static String buildFindAllSql() {
        return
            baseSelectString() +
            " WHERE NOT (o.state = ?)";
    }

    public static String buildFindAllClientSql() {
        return 
            baseSelectString() + 
            " WHERE NOT (c.category = ?) AND NOT (c.state = ?) AND NOT (o.state = ?)";
    }

    public static String buildDownloadCsvOfficeForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return
            baseSelectString() +
            " WHERE office_id IN (" + placeholders + ") \" + NOT (state = ?)";
    }
}
