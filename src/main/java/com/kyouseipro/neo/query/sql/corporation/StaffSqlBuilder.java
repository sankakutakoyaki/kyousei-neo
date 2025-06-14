package com.kyouseipro.neo.query.sql.corporation;

import com.kyouseipro.neo.common.Utilities;

public class StaffSqlBuilder {

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  staff_id INT, company_id INT, office_id INT, " +
            "  name NVARCHAR(255), name_kana NVARCHAR(255), phone_number NVARCHAR(255), email NVARCHAR(255), " +
            "  version INT, state INT" +
            "); ";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.staff_id, INSERTED.company_id, INSERTED.office_id, " +
            "INSERTED.name, INSERTED.name_kana, INSERTED.phone_number, INSERTED.email, INSERTED.version, INSERTED.state ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO staffs_log (" +
            "  staff_id, editor, process, log_date, company_id, office_id, " +
            "  name, name_kana, phone_number, email, version, state" +
            ") " +
            "SELECT staff_id, ?, '" + processName + "', CURRENT_TIMESTAMP, " +
            "  company_id, office_id, name, name_kana, phone_number, email, version, state " +
            "FROM " + rowTableName + ";";
    }

    public static String buildInsertStaffSql() {
        return
            buildLogTableSql("@Inserted") +

            "INSERT INTO staffs (" +
            "  company_id, office_id, name, name_kana, phone_number, email, version, state" +
            ") " +
            buildOutputLogSql() + "INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql("@Inserted", "INSERT") +
            "SELECT staff_id FROM @Inserted;";
    }

    public static String buildUpdateStaffSql() {
        return
            buildLogTableSql("@Updated") +

            "UPDATE staffs SET " +
            "  company_id=?, office_id=?, name=?, name_kana=?, phone_number=?, email=?, version=?, state=? " +
            buildOutputLogSql() + "INTO @Updated " +
            "WHERE staff_id=?; " +

            buildInsertLogSql("@Updated", "UPDATE") +
            "SELECT staff_id FROM @Updated;";
    }

    public static String buildDeleteStaffForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count);

        return
            buildLogTableSql("@Deleted") +

            "UPDATE staffs SET state = ? " +
            buildOutputLogSql() + "INTO @Deleted " +
            "WHERE staff_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLogSql("@Deleted", "DELETE") +
            "SELECT staff_id FROM @Deleted;";
    }

    private static String baseSelectString() {
        return
            "SELECT s.*, c.name as company_name, o.name as office_name FROM staffs s" + 
            " INNER JOIN companies c ON c.company_id = s.company_id AND NOT (c.state = ?)" + 
            " LEFT OUTER JOIN offices o ON o.office_id = s.office_id AND NOT (o.state = ?)";
    }

    public static String buildFindByIdSql() {
        return
            baseSelectString() +
            " WHERE NOT (s.state = ?) AND s.staff_id = ?";
    }

    public static String buildFindAllSql() {
        return 
            baseSelectString() +
            " WHERE NOT (c.category = 0) AND NOT (s.state = ?)";
    }

    public static String buildDownloadCsvStaffForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return
            baseSelectString() +
            " WHERE s.staff_id IN (" + placeholders + ") AND NOT (s.state = ?)";
    }
}
