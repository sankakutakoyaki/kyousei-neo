package com.kyouseipro.neo.qualification.repository;

import com.kyouseipro.neo.common.Utilities;

public class QualificationsSqlBuilder {

    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  qualifications_id INT, owner_id INT, owner_category INT, qualification_master_id INT, number NVARCHAR(255), " +
            "  acquisition_date DATE, expiry_date DATE, version INT, state INT" +
            "); ";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
        return
            "INSERT INTO qualifications_log (" +
            "  qualifications_id, editor, process, log_date, owner_id, owner_category, qualification_master_id, number, " +
            "  acquisition_date, expiry_date, version, state" +
            ") " +
            "SELECT qualifications_id, ?, '" + processName + "', CURRENT_TIMESTAMP, owner_id, owner_category, qualification_master_id, number, " +
            "  acquisition_date, expiry_date, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.qualifications_id, INSERTED.owner_id, INSERTED.owner_category, INSERTED.qualification_master_id, " +
            "  INSERTED.number, INSERTED.acquisition_date, INSERTED.expiry_date, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsert() {
        return
            buildLogTable("@Inserted") +

            "INSERT INTO qualifications (owner_id, owner_category, qualification_master_id, number, acquisition_date, expiry_date, version, state) " +
            buildOutputLog() + "INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLog("@Inserted", "INSERT") +
            "SELECT qualifications_id FROM @Inserted;";
    }

    public static String buildUpdate() {
        return
            buildLogTable("@Updated") +

            "UPDATE qualifications SET owner_id=?, owner_category=?, qualification_master_id=?, number=?, acquisition_date=?, expiry_date=?, version=?, state=? " +
            buildOutputLog() + "INTO @Updated " +
            "WHERE qualifications_id=? AND version=?; " +

            buildInsertLog("@Updated", "UPDATE") +
            "SELECT qualifications_id FROM @Updated;";
    }

    public static String buildDeleteByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count);

        return
            buildLogTable("@Deleted") +

            "UPDATE qualifications SET state = ? " +
            buildOutputLog() + "INTO @Deleted " +
            "WHERE qualifications_id IN (" + placeholders + ") AND NOT (state = ?);" +

            buildInsertLog("@Deleted", "DELETE") +
            "SELECT qualifications_id FROM @Deleted;";
    }

    public static String buildDelete() {
        return
            buildLogTable("@Deleted") +

            "UPDATE qualifications SET state=? " +
            buildOutputLog() + "INTO @Deleted " +
            "WHERE qualifications_id=?; " +

            buildInsertLog("@Deleted", "DELETE") +
            "SELECT qualifications_id FROM @Deleted;";
    }

    public static String buildFindByIdForEmployee() {
        return
            baseEmployeeStatusSelectString() +
            " WHERE NOT (q.state = ?) AND q.owner_id = ? AND q.owner_category = ? ORDER BY qm.code";
    }

    public static String buildFindByIdForCompany() {
        return
            baseCompanyStatusSelectString() +
            " WHERE NOT (q.state = ?) AND q.owner_id = ? AND q.owner_category = ? ORDER BY qm.code";
    }

    private static String baseEmployeeSelectString() {
        return
            "SELECT q.qualifications_id, q.owner_id, q.owner_category, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, '取得済み' as status, q.is_enabled" +
            ", q.version, q.state, e.full_name as owner_name, e.full_name_kana as owner_name_kana, qm.name as qualification_name FROM qualifications q" +
            " LEFT OUTER JOIN employees e ON e.employee_id = q.owner_id AND NOT (e.state = ?) " +
            " LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = ?)";
    }

    private static String baseCompanySelectString() {
        return
            "SELECT q.qualifications_id, q.owner_id, q.owner_category, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, '取得済み' as status, q.is_enabled" +
            ", q.version, q.state, c.name as owner_name, c.name_kana as owner_name_kana, qm.name as qualification_name FROM qualifications q" +
            " LEFT OUTER JOIN companies c ON c.company_id = q.owner_id AND NOT (c.state = ?) " +
            " LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = ?)";
    }

    private static String baseEmployeeStatusSelectString() {
        return
            "SELECT e.employee_id as owner_id, q.owner_category, e.full_name as owner_name, e.full_name_kana as owner_name_kana, qm.name as qualification_name" +
            ", q.qualifications_id, qm.qualification_master_id, q.number, q.is_enabled, q.version, q.state" +
            ", COALESCE(q.acquisition_date, '9999-12-31') as acquisition_date, COALESCE(q.expiry_date, '9999-12-31') as expiry_date" +
            ", CASE WHEN q.owner_id IS NOT NULL THEN '取得済み' ELSE '未取得' END AS status FROM employees e" +
            " CROSS JOIN qualification_master qm" +
            " LEFT JOIN qualifications q ON q.owner_id = e.employee_id AND q.qualification_master_id = qm.qualification_master_id" +
            " AND NOT (q.state = ?) AND q.owner_category = 0";
    }

    private static String baseCompanyStatusSelectString() {
        return
            "SELECT c.company_id as owner_id, q.owner_category, c.name as owner_name, c.name_kana as owner_name_kana, qm.name as qualification_name" +
            ", q.qualifications_id, qm.qualification_master_id, q.number, q.is_enabled, q.version, q.state" +
            ", COALESCE(q.acquisition_date, '9999-12-31') as acquisition_date, COALESCE(q.expiry_date, '9999-12-31') as expiry_date" +
            ", CASE WHEN q.owner_id IS NOT NULL THEN '取得済み' ELSE '未取得' END AS status FROM companies c" +
            " CROSS JOIN qualification_master qm" +
            " LEFT JOIN qualifications q ON q.owner_id = c.company_id AND q.qualification_master_id = qm.qualification_master_id" +
            " AND NOT (q.state = ?) AND owner_category = ?";
    }

    public static String buildFindAllByEmployeeId() {
        return  
            baseEmployeeSelectString() +
            " WHERE NOT (q.state = ?) AND e.employee_id = ? AND q.owner_category = 0" +
            " ORDER BY qm.code";
    }

    public static String buildFindAllByCompanyId() {
        return  
            baseCompanySelectString() +
            " WHERE NOT (q.state = ?) AND c.company_id = ? AND c.category = ? AND q.owner_category = ?" +
            " AND qm.category_name = '許可'" +
            " ORDER BY qm.code";
    }

    public static String buildFindAllByEmployeeStatus() {
        return  
            baseEmployeeStatusSelectString() +
            " WHERE NOT (qm.state = ?) AND NOT (e.state = ?)" +
            " ORDER BY qm.code, e.employee_id";
    }

    public static String buildFindAllByCompanyStatus() {
        return  
            baseCompanyStatusSelectString() +
            " WHERE NOT (c.state = ?) AND NOT (qm.state = ?) AND c.category = ?" +
            " AND qm.category_name = '許可'" +
            " ORDER BY qm.code, c.company_id";
    }


    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return "SELECT * FROM qualifications WHERE qualifications_id IN (" + placeholders + ") \" + NOT (state = ?)";
    }
}
