package com.kyouseipro.neo.query.sql.corporation;

import com.kyouseipro.neo.common.Utilities;

public class CompanySqlBuilder {

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  company_id INT, category NVARCHAR(255), name NVARCHAR(255), name_kana NVARCHAR(255), " +
            "  tel_number NVARCHAR(255), fax_number NVARCHAR(255), postal_code NVARCHAR(255), " +
            "  full_address NVARCHAR(255), email NVARCHAR(255), web_address NVARCHAR(255), " +
            "  version INT, state INT" +
            "); ";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.company_id, INSERTED.category, INSERTED.name, INSERTED.name_kana, " +
            "INSERTED.tel_number, INSERTED.fax_number, INSERTED.postal_code, INSERTED.full_address, " +
            "INSERTED.email, INSERTED.web_address, INSERTED.version, INSERTED.state ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO companies_log (" +
            "  company_id, editor, process, log_date, category, name, name_kana, tel_number, fax_number, " +
            "  postal_code, full_address, email, web_address, version, state" +
            ") " +
            "SELECT company_id, ?, '" + processName + "', CURRENT_TIMESTAMP, category, name, name_kana, tel_number, fax_number, " +
            "  postal_code, full_address, email, web_address, version, state " +
            "FROM " + rowTableName + ";";
    }

    public static String buildInsertCompanySql() {
        return
            buildLogTableSql("@Inserted") +

            "INSERT INTO companies (" +
            "  category, name, name_kana, tel_number, fax_number, postal_code, full_address, " +
            "  email, web_address, version, state" +
            ") " +
            buildOutputLogSql() + "INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql("@Inserted", "INSERT") +
            "SELECT company_id FROM @Inserted;";
    }

    public static String buildUpdateCompanySql() {
        return
            buildLogTableSql("@Updated") +

            "UPDATE companies SET " +
            "  category=?, name=?, name_kana=?, tel_number=?, fax_number=?, postal_code=?, " +
            "  full_address=?, email=?, web_address=?, version=?, state=? " +
            buildOutputLogSql() + "INTO @Updated " +
            "WHERE company_id=?; " +

            buildInsertLogSql("@Updated", "UPDATE") +
            "SELECT company_id FROM @Updated;";
    }

    public static String buildDeleteCompanyForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count);

        return
            buildLogTableSql("@Deleted") +

            "UPDATE companies SET state = ? " +
            buildOutputLogSql() + "INTO @Deleted " +
            "WHERE company_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLogSql("@Deleted", "DELETE") +
            "SELECT company_id FROM @Deleted;";
    }

    public static String buildFindByIdSql() {
        return "SELECT * FROM companies WHERE company_id = ? AND NOT (state = ?)";
    }

    public static String buildFindAllSql() {
        return "SELECT * FROM companies WHERE NOT (state = ?)";
    }

    public static String buildFindAllClientSql() {
        return "SELECT * FROM companies WHERE NOT (category = ?) AND NOT (state = ?)";
    }

    public static String buildDownloadCsvCompanyForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return "SELECT * FROM companies WHERE company_id IN (" + placeholders + ") AND NOT (state = ?)";
    }
}
