package com.kyouseipro.neo.query.sql;

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
}
