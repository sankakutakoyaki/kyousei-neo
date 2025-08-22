package com.kyouseipro.neo.query.sql.qualification;

import com.kyouseipro.neo.common.Utilities;

public class QualificationsSqlBuilder {

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  qualifications_id INT, owner_id INT, owner_category INT, qualification_master_id INT, number NVARCHAR(255), " +
            "  acquisition_date DATE, expiry_date DATE, version INT, state INT" +
            "); ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO qualifications_log (" +
            "  qualifications_id, editor, process, log_date, owner_id, owner_category, qualification_master_id, number, " +
            "  acquisition_date, expiry_date, version, state" +
            ") " +
            "SELECT qualifications_id, ?, '" + processName + "', CURRENT_TIMESTAMP, owner_id, owner_category, qualification_master_id, number, " +
            "  acquisition_date, expiry_date, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.qualifications_id, INSERTED.owner_id, INSERTED.owner_category, INSERTED.qualification_master_id, " +
            "  INSERTED.number, INSERTED.acquisition_date, INSERTED.expiry_date, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertQualificationsSql() {
        return
            buildLogTableSql("@Inserted") +

            "INSERT INTO qualifications (owner_id, owner_category, qualification_master_id, number, acquisition_date, expiry_date, version, state) " +
            buildOutputLogSql() + "INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql("@Inserted", "INSERT") +
            "SELECT qualifications_id FROM @Inserted;";
    }

    public static String buildUpdateQualificationsSql() {
        return
            buildLogTableSql("@Updated") +

            "UPDATE qualifications SET owner_id=?, owner_category=?, qualification_master_id=?, number=?, acquisition_date=?, expiry_date=?, version=?, state=? " +
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
            "WHERE qualifications_id IN (" + placeholders + ") AND NOT (state = ?);" +

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

    public static String buildFindByIdForEmployeeSql() {
        return
            baseEmployeeStatusSelectString() +
            " WHERE NOT (q.state = ?) AND q.owner_id = ? AND q.owner_category = ?";
    }

    public static String buildFindByIdForCompanySql() {
        return
            baseCompanyStatusSelectString() +
            " WHERE NOT (q.state = ?) AND q.owner_id = ? AND q.owner_category = ?";
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
            " AND NOT (q.state = ?)";
    }

    private static String baseCompanyStatusSelectString() {
        return
            "SELECT c.company_id as owner_id, q.owner_category, c.name as owner_name, c.name_kana as owner_name_kana, qm.name as qualification_name" +
            ", q.qualifications_id, qm.qualification_master_id, q.number, q.is_enabled, q.version, q.state" +
            ", COALESCE(q.acquisition_date, '9999-12-31') as acquisition_date, COALESCE(q.expiry_date, '9999-12-31') as expiry_date" +
            ", CASE WHEN q.owner_id IS NOT NULL THEN '取得済み' ELSE '未取得' END AS status FROM companies c" +
            " CROSS JOIN qualification_master qm" +
            " LEFT JOIN qualifications q ON q.owner_id = c.company_id AND q.qualification_master_id = qm.qualification_master_id" +
            " AND NOT (q.state = ?)";
    //     sb.append(" WHERE NOT (c.state = " + Enums.state.DELETE.getCode() + ") AND c.category = " + Enums.clientCategory.PARTNER.getCode() + " AND qm.category_name = '許可'");
    //     sb.append(" ORDER BY qm.qualification_master_id, c.company_id");
    //     return sb.toString();
    }

    public static String buildFindAllEmployeeIdSql() {
        return  
            baseEmployeeSelectString() +
            " WHERE NOT (q.state = ?) AND e.employee_id = ?" +
            " ORDER BY qm.code";
    }

    public static String buildFindAllCompanyIdSql() {
        return  
            baseCompanySelectString() +
            " WHERE NOT (q.state = ?) AND c.company_id = ? AND c.category = ?" +
            " AND qm.category_name = '許可'" +
            " ORDER BY qm.code";
    }

    public static String buildFindAllEmployeeStatusSql() {
        return  
            baseEmployeeStatusSelectString() +
            " WHERE NOT (qm.state = ?) AND NOT (e.state = ?)" +
            " ORDER BY qm.qualification_master_id, e.employee_id";
    }

    public static String buildFindAllCompanyStatusSql() {
        return  
            baseCompanyStatusSelectString() +
            " WHERE NOT (c.state = ?) AND NOT (qm.state = ?) AND c.category = ?" +
            " AND qm.category_name = '許可'" +
            " ORDER BY qm.qualification_master_id, c.company_id";
    }


    public static String buildDownloadCsvQualificationsForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return "SELECT * FROM qualifications WHERE qualifications_id IN (" + placeholders + ") \" + NOT (state = ?)";
    }

    public static String buildFindAllComboQualificationMasterSql() {
        return "SELECT qualification_master_id as number, name as text FROM qualification_master WHERE NOT (state = ?) ORDER BY code;";
    }

    public static String buildFindAllComboLicenseMasterSql() {
        return "SELECT qualification_master_id as number, name as text FROM qualification_master WHERE NOT (state = ?) AND category_name = '許可' ORDER BY code;";
    }
}
