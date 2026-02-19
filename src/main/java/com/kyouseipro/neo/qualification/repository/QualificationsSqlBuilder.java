package com.kyouseipro.neo.qualification.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.corporation.company.entity.CompanyEntityRequest;
import com.kyouseipro.neo.qualification.entity.QualificationsEntityRequest;
import com.kyouseipro.neo.recycle.regist.entity.RecycleEntityRequest;

public class QualificationsSqlBuilder {

    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  qualifications_id INT, employee_id INT, company_id INT, qualification_master_id INT, number NVARCHAR(255), " +
            "  acquisition_date DATE, expiry_date DATE, version INT, state INT" +
            "); ";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
        return
            "INSERT INTO qualifications_log (" +
            "  qualifications_id, editor, process, log_date, employee_id, company_id, qualification_master_id, number, " +
            "  acquisition_date, expiry_date, version, state" +
            ") " +
            "SELECT qualifications_id, ?, '" + processName + "', CURRENT_TIMESTAMP, employee_id, company_id, qualification_master_id, number, " +
            "  acquisition_date, expiry_date, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.qualifications_id, INSERTED.employee_id, INSERTED.company_id, INSERTED.qualification_master_id, " +
            "  INSERTED.number, INSERTED.acquisition_date, INSERTED.expiry_date, INSERTED.version, INSERTED.state ";
    }

    // public static String buildInsert() {
    //     return
    //         buildLogTable("@Inserted") +

    //         "INSERT INTO qualifications (employee_id, company_id, qualification_master_id, number, acquisition_date, expiry_date, version, state) " +
    //         buildOutputLog() + "INTO @Inserted " +
    //         "VALUES (?, ?, ?, ?, ?, ?, ?, ?); " +

    //         buildInsertLog("@Inserted", "INSERT") +
    //         "SELECT qualifications_id FROM @Inserted;";
    // }

    // public static String buildUpdate() {
    //     return
    //         buildLogTable("@Updated") +

    //         "UPDATE qualifications SET employee_id=?, company_id=?, qualification_master_id=?, number=?, acquisition_date=?, expiry_date=?, version=?, state=? " +
    //         buildOutputLog() + "INTO @Updated " +
    //         "WHERE qualifications_id=? AND version=?; " +

    //         buildInsertLog("@Updated", "UPDATE") +
    //         "SELECT qualifications_id FROM @Updated;";
    // }

    public static String buildBulkInsert(QualificationsEntityRequest req) {

        StringBuilder sql = new StringBuilder();
        sql.append(buildLogTable("@InsertedRows"));
        sql.append("INSERT INTO qualifications (");

        List<String> sets = new ArrayList<>();

        if (req.getEmployeeId() != null) {
            sets.add("employee_id");
        }
        if (req.getCompanyId() != null) {
            sets.add("company_id");
        }
        if (req.getQualificationMasterId() != null) {
            sets.add("qualification_master_id");
        }
        if (req.getNumber() != null) {
            sets.add("number");
        }
        if (req.getAcquisitionDate() != null) {
            sets.add("acquisition_date");
        }
        if (req.getExpiryDate() != null) {
            sets.add("expiry_date");
        }


        if (sets.isEmpty()) {
            throw new IllegalArgumentException("更新項目がありません");
        }

        sql.append(String.join(", ", sets));
        sql.append(") ");

        sql.append(buildOutputLog() + "INTO @InsertedRows ");

        sql.append("VALUES (");
        sql.append(sets.stream().map(v -> "?").collect(Collectors.joining(",")));
        sql.append(");");

        sql.append(buildInsertLog("@InsertedRows", "INSERT"));
        sql.append("SELECT qualifications_id FROM @InsertedRows;");

        return  sql.toString();
    }

    public static String buildBulkUpdate(QualificationsEntityRequest req) {

        StringBuilder sql = new StringBuilder();
        sql.append(buildLogTable("@UpdatedRows"));
        sql.append("UPDATE qualifications SET ");

        List<String> sets = new ArrayList<>();

        if (req.getEmployeeId() != null) {
            sets.add("employee_id=?");
        }
        if (req.getCompanyId() != null) {
            sets.add("company_id=?");
        }
        if (req.getQualificationMasterId() != null) {
            sets.add("qualification_master_id=?");
        }
        if (req.getNumber() != null) {
            sets.add("number=?");
        }
        if (req.getAcquisitionDate() != null) {
            sets.add("acquisition_date=?");
        }
        if (req.getExpiryDate() != null) {
            sets.add("expiry_date=?");
        }

        // 共通更新
        sets.add("version = version + 1");
        sets.add("update_date = SYSDATETIME()");

        if (sets.isEmpty()) {
            throw new IllegalArgumentException("No update fields");
        }

        sql.append(String.join(", ", sets));

        sql.append(buildOutputLog() + "INTO @UpdatedRows ");
        sql.append(" WHERE qualifications_id = ? AND version = ? AND state <> ?;");
        sql.append(buildInsertLog("@UpdatedRows", "UPDATE"));

        return 
            sql.toString();
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
            " WHERE NOT (q.state = ?) AND q.employee_id = ? AND q.company_id = ? ORDER BY qm.code";
    }

    public static String buildFindByIdForCompany() {
        return
            baseCompanyStatusSelectString() +
            " WHERE NOT (q.state = ?) AND q.employee_id = ? AND q.company_id = ? ORDER BY qm.code";
    }

    private static String baseEmployeeSelectString() {
        return
            "SELECT q.qualifications_id, q.employee_id, q.company_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, '取得済み' as status, q.is_enabled" +
            ", q.version, q.state, e.full_name as owner_name, e.full_name_kana as owner_name_kana, qm.name as qualification_name FROM qualifications q" +
            " LEFT OUTER JOIN employees e ON e.employee_id = q.employee_id AND NOT (e.state = ?) " +
            " LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = ?)";
    }

    private static String baseCompanySelectString() {
        return
            "SELECT q.qualifications_id, q.employee_id, q.company_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, '取得済み' as status, q.is_enabled" +
            ", q.version, q.state, c.name as owner_name, c.name_kana as owner_name_kana, qm.name as qualification_name FROM qualifications q" +
            " LEFT OUTER JOIN companies c ON c.company_id = q.company_id AND NOT (c.state = ?) " +
            " LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = ?)";
    }

    private static String baseEmployeeStatusSelectString() {
        return
            "SELECT e.employee_id as employee_id, q.company_id, e.full_name as owner_name, e.full_name_kana as owner_name_kana, qm.name as qualification_name" +
            ", q.qualifications_id, qm.qualification_master_id, q.number, q.is_enabled, q.version, q.state, q.acquisition_date, q.expiry_date" +
            // ", COALESCE(q.acquisition_date, '9999-12-31') as acquisition_date, COALESCE(q.expiry_date, '9999-12-31') as expiry_date" +
            ", CASE WHEN q.employee_id IS NOT NULL THEN '取得済み' ELSE '未取得' END AS status FROM employees e" +
            " CROSS JOIN qualification_master qm" +
            " LEFT JOIN qualifications q ON q.employee_id = e.employee_id AND q.qualification_master_id = qm.qualification_master_id" +
            " AND q.state <> ? AND q.company_id IS NULL";
    }

    private static String baseCompanyStatusSelectString() {
        return
            "SELECT c.company_id as employee_id, q.company_id, c.name as owner_name, c.name_kana as owner_name_kana, qm.name as qualification_name" +
            ", q.qualifications_id, qm.qualification_master_id, q.number, q.is_enabled, q.version, q.state, q.acquisition_date, q.expiry_date" +
            // ", COALESCE(q.acquisition_date, '9999-12-31') as acquisition_date, COALESCE(q.expiry_date, '9999-12-31') as expiry_date" +
            ", CASE WHEN q.employee_id IS NOT NULL THEN '取得済み' ELSE '未取得' END AS status FROM companies c" +
            " CROSS JOIN qualification_master qm" +
            " LEFT JOIN qualifications q ON q.employee_id = c.company_id AND q.qualification_master_id = qm.qualification_master_id" +
            " AND q.state <> ? AND employee_id IS NULL";
    }


    public static String buildFindAllByEmployee() {
        return  
            baseEmployeeSelectString() +
            " WHERE q.state <> ? AND q.company_id IS NULL" +
            " AND qm.category_name <> '許可'" +
            " ORDER BY qm.category_name, qm.name";
    }

    public static String buildFindAllByCompany() {
        return  
            baseCompanySelectString() +
            " WHERE q.state <> ? AND q.employee_id IS NULL" +
            " AND qm.category_name = '許可'" +
            " ORDER BY qm.category_name, qm.name";
    }

    public static String buildFindByQualificationsIdFromEmployee() {
        return  
            baseEmployeeSelectString() +
            " WHERE q.state <> ? AND q.qualifications_id = ?";
    }

    public static String buildFindByQualificationsIdFromCompany() {
        return  
            baseCompanySelectString() +
            " WHERE q.state <> ? AND q.qualifications_id = ?";
    }

    public static String buildFindAllByEmployeeId() {
        return  
            baseEmployeeSelectString() +
            " WHERE q.state <> ? AND e.employee_id = ? AND q.company_id IS NULL" +
            " AND qm.category_name <> '許可'" +
            " ORDER BY qm.category_name, qm.name";
    }

    public static String buildFindAllByCompanyId() {
        return  
            baseCompanySelectString() +
            " WHERE q.state <> ? AND c.company_id = ?  AND q.employee_id IS NULL" +
            " AND qm.category_name = '許可'" +
            " ORDER BY qm.category_name, qm.name";
    }

    public static String buildFindAllByEmployeeStatus() {
        return  
            baseEmployeeStatusSelectString() +
            " WHERE qm.state = <> ? AND e.state <> ?" +
            " ORDER BY qm.category_name, qm.name, e.employee_id";
    }

    public static String buildFindAllByCompanyStatus() {
        return  
            baseCompanyStatusSelectString() +
            " WHERE c.state <> ? AND qm.state <> ? " +
            " AND qm.category_name = '許可'" +
            " ORDER BY qm.category_name, qm.name, c.company_id";
    }


    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return "SELECT * FROM qualifications WHERE qualifications_id IN (" + placeholders + ") AND state <> ?";
    }
}
