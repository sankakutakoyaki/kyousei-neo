package com.kyouseipro.neo.query.sql.qualification;

import com.kyouseipro.neo.common.Utilities;

public class QualificationsSqlBuilder {

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  qualifications_id INT, owner_id INT, qualification_master_id INT, number NVARCHAR(255), " +
            "  acquisition_date DATE, expiry_date DATE, version INT, state INT" +
            "); ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO qualifications_log (" +
            "  qualifications_id, editor, process, log_date, owner_id, qualification_master_id, number, " +
            "  acquisition_date, expiry_date, version, state" +
            ") " +
            "SELECT qualifications_id, ?, '" + processName + "', CURRENT_TIMESTAMP, owner_id, qualification_master_id, number, " +
            "  acquisition_date, expiry_date, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.qualifications_id, INSERTED.owner_id, INSERTED.qualification_master_id, " +
            "  INSERTED.number, INSERTED.acquisition_date, INSERTED.expiry_date, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertQualificationsSql() {
        return
            buildLogTableSql("@Inserted") +

            "INSERT INTO qualifications (owner_id, qualification_master_id, number, acquisition_date, expiry_date, version, state) " +
            buildOutputLogSql() + "INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql("@Inserted", "INSERT") +
            "SELECT qualifications_id FROM @Inserted;";
    }

    public static String buildUpdateQualificationsSql() {
        return
            buildLogTableSql("@Updated") +

            "UPDATE qualifications SET owner_id=?, qualification_master_id=?, number=?, acquisition_date=?, expiry_date=?, version=?, state=? " +
            buildOutputLogSql() + "INTO @Updated " +
            "WHERE qualifications_id=?; " +

            buildInsertLogSql("@Updated", "UPDATE") +
            "SELECT qualifications_id FROM @Updated;";
    }

    public static String buildDeleteQualificationsForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count);

        return
            buildLogTableSql("@Deleted") +

            "UPDATE qualifications SET state = ? " +
            buildOutputLogSql() + "INTO @Deleted " +
            "WHERE qualifications_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLogSql("@Deleted", "DELETE") +
            "SELECT qualifications_id FROM @Deleted;";
    }

    public static String buildDeleteQualificationsSql() {
        return
            buildLogTableSql("@Deleted") +

            "UPDATE qualifications SET state=? " +
            buildOutputLogSql() + "INTO @Deleted " +
            "WHERE qualifications_id=?; " +

            buildInsertLogSql("@Deleted", "DELETE") +
            "SELECT qualifications_id FROM @Deleted;";
    }

    public static String buildFindByIdSql() {
        return "SELECT * FROM qualifications WHERE NOT (state = ?) AND qualifiations_id = ?";
    }

    public static String buildFindAllEmployeeIdSql() {
        return  
            "SELECT q.qualifications_id, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.is_enabled" +
            ", q.version, q.state, e.full_name as owner_name, qm.name as qualification_name FROM qualifications q" +
            " LEFT OUTER JOIN employees e ON e.employee_id = q.owner_id AND NOT (e.state = ?) " +
            " LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = ?)" +
            " WHERE NOT (q.state = ?) AND e.employee_id = ?";
    }

    public static String buildFindAllCompanyIdSql() {
        return  
            "SELECT q.qualifications_id, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.is_enabled" +
            ", q.version, q.state, c.name as owner_name, qm.name as qualification_name FROM qualifications q" +
            " LEFT OUTER JOIN companies c ON c.company_id = q.owner_id AND NOT (c.state = ?) " +
            " LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = ?)" +
            " WHERE NOT (q.state = ?) AND c.company_id = ?";
    }

        public static String buildFindAllEmployeeSql() {
        return  
            "SELECT q.qualifications_id, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.is_enabled" +
            ", q.version, q.state, e.full_name as owner_name, qm.name as qualification_name FROM qualifications q" +
            " LEFT OUTER JOIN employees e ON e.employee_id = q.owner_id AND NOT (e.state = ?) " +
            " LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = ?)" +
            " WHERE NOT (q.state = ?)";
    }

    public static String buildFindAllCompanySql() {
        return  
            "SELECT q.qualifications_id, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.is_enabled" +
            ", q.version, q.state, c.name as owner_name, qm.name as qualification_name FROM qualifications q" +
            " LEFT OUTER JOIN companies c ON c.company_id = q.owner_id AND NOT (c.state = ?) " +
            " LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = ?)" +
            " WHERE NOT (q.state = ?)";
    }


    public static String buildDownloadCsvQualificationsForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return "SELECT * FROM qualifications WHERE qualifications_id IN (" + placeholders + ") \" + NOT (state = ?)";
    }

    public static String buildFindAllComboQualificationsSql() {
        return "SELECT qualifications_id as number, name as text FROM qualifications WHERE NOT (state = ?);";
    }
}
