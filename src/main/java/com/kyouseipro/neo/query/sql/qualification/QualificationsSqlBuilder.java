package com.kyouseipro.neo.query.sql.qualification;

import com.kyouseipro.neo.common.Utilities;

public class QualificationsSqlBuilder {

    public static String buildInsertQualificationsSql() {
        return
            "DECLARE @Inserted TABLE (qualifications_id INT);" +
            "INSERT INTO qualifications (owner_id, qualification_master_id, number, acquisition_date, expiry_date, version, state) " +
            "OUTPUT INSERTED.qualifications_id INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?);" +
            "INSERT INTO qualifications_log (qualifications_id, editor, process, log_date, owner_id, qualification_master_id, number, acquisition_date, expiry_date, version, state) " +
            "SELECT q.qualifications_id, ?, 'INSERT', CURRENT_TIMESTAMP, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.version, q.state " +
            "FROM qualifications q INNER JOIN @Inserted i ON q.qualifications_id = i.qualifications_id;" +
            "SELECT qualifications_id FROM @Inserted;";
    }

    public static String buildUpdateQualificationsSql() {
        return
            "DECLARE @Updated TABLE (qualifications_id INT);" +
            "UPDATE qualifications SET owner_id=?, qualification_master_id=?, number=?, acquisition_date=?, expiry_date=?, version=?, state=? " +
            "OUTPUT INSERTED.qualifications_id INTO @Updated " +
            "WHERE qualifications_id=?;" +
            "INSERT INTO qualifications_log (qualifications_id, editor, process, log_date, owner_id, qualification_master_id, number, acquisition_date, expiry_date, version, state) " +
            "SELECT q.qualifications_id, ?, 'UPDATE', CURRENT_TIMESTAMP, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.version, q.state " +
            "FROM qualifications q INNER JOIN @Updated u ON q.qualifications_id = u.qualifications_id;" +
            "SELECT qualifications_id FROM @Updated;";
    }

    public static String buildDeleteQualificationsSql() {
        return
            "DECLARE @Deleted TABLE (qualifications_id INT);" +
            "UPDATE qualifications SET state=? " +
            "OUTPUT INSERTED.qualifications_id INTO @Deleted " +
            "WHERE qualifications_id=?;" +
            "INSERT INTO qualifications_log (qualifications_id, editor, process, log_date, owner_id, qualification_master_id, number, acquisition_date, expiry_date, version, state) " +
            "SELECT q.qualifications_id, ?, 'DELETE', CURRENT_TIMESTAMP, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.version, q.state " +
            "FROM qualifications q INNER JOIN @Deleted d ON q.qualifications_id = d.qualifications_id;" +
            "SELECT qualifications_id FROM @Deleted;";
    }

    public static String buildFindByIdSql() {
        return "SELECT * FROM qualifications WHERE qualifiations_id = ? AND NOT (state = ?)";
    }

    public static String buildFindAllEmployeeSql() {
        // return "SELECT * FROM qualifications WHERE qualifiations_id = ? AND NOT (state = ?)";
        return  
            "SELECT q.qualifications_id, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.is_enabled" +
            ", q.version, q.state, e.full_name as owner_name, qm.name as qualification_name FROM qualifications q" +
            " LEFT OUTER JOIN employees e ON e.employee_id = q.owner_id AND NOT (e.state = ?) " +
            " LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = ?)";
    }

    public static String buildFindAllCompanySql() {
        // return "SELECT * FROM qualifications WHERE qualifiations_id = ? AND NOT (state = ?)";
        return  
            "SELECT q.qualifications_id, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.is_enabled" +
            ", q.version, q.state, c.name as owner_name, qm.name as qualification_name FROM qualifications q" +
            " LEFT OUTER JOIN companies c ON c.company_id = q.owner_id AND NOT (c.state = ?) " +
            " LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = ?)";
    }

    public static String buildDeleteQualificationsForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return
            "DECLARE @DeletedRows TABLE (qualifications_id INT); " +

            "UPDATE qualifications " +
            "SET state = ? " +
            "OUTPUT INSERTED.qualifications_id INTO @DeletedRows " +
            "WHERE qualifications_id IN (" + placeholders + ") " +
            "AND NOT (state = ?); " +

            "INSERT INTO qualifications_log (qualifications_id, editor, process, log_date, owner_id, qualification_master_id, number, acquisition_date, expiry_date, version, state) " +
            "SELECT q.qualifications_id, ?, 'DELETE', CURRENT_TIMESTAMP, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.version, q.state " +
            "FROM qualifications q INNER JOIN @Updated u ON q.qualifications_id = u.qualifications_id;";
    }

    public static String buildDownloadCsvQualificationsForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return "SELECT * FROM qualifications WHERE qualifications_id IN (" + placeholders + ") \" + NOT (state = ?)";
    }

    // public static String buildFindAllSql() {
    //     return "SELECT * FROM qualifications WHERE NOT (state = ?)";
    // }
}
