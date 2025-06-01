package com.kyouseipro.neo.query.sql;

public class QualificationFilesSqlBuilder {

    public static String buildInsertQualificationFilesSql() {
        return
            "DECLARE @InsertedRows TABLE (qualifications_files_id INT);" +
            "INSERT INTO qualifications_files (qualifications_id, file_name, internal_name, folder_name, version, state) " +
            "OUTPUT INSERTED.qualifications_files_id INTO @InsertedRows " +
            "VALUES (?, ?, ?, ?, ?, ?);" +
            "INSERT INTO qualifications_files_log (qualifications_files_id, editor, process, log_date, " +
            "qualifications_id, file_name, internal_name, folder_name, version, state) " +
            "SELECT qf.qualifications_files_id, ?, 'INSERT', CURRENT_TIMESTAMP, " +
            "qf.qualifications_id, qf.file_name, qf.internal_name, qf.folder_name, qf.version, qf.state " +
            "FROM qualifications_files qf INNER JOIN @InsertedRows ir ON qf.qualifications_files_id = ir.qualifications_files_id;" +
            "SELECT qualifications_files_id FROM @InsertedRows;";
    }

    public static String buildUpdateQualificationFilesSql() {
        return
            "DECLARE @UpdatedRows TABLE (qualifications_files_id INT);" +
            "UPDATE qualifications_files SET " +
            "qualifications_id=?, file_name=?, internal_name=?, folder_name=?, version=?, state=? " +
            "OUTPUT INSERTED.qualifications_files_id INTO @UpdatedRows " +
            "WHERE qualifications_files_id=?;" +
            "INSERT INTO qualifications_files_log (qualifications_files_id, editor, process, log_date, " +
            "qualifications_id, file_name, internal_name, folder_name, version, state) " +
            "SELECT qf.qualifications_files_id, ?, 'UPDATE', CURRENT_TIMESTAMP, " +
            "qf.qualifications_id, qf.file_name, qf.internal_name, qf.folder_name, qf.version, qf.state " +
            "FROM qualifications_files qf INNER JOIN @UpdatedRows ur ON qf.qualifications_files_id = ur.qualifications_files_id;" +
            "SELECT qualifications_files_id FROM @UpdatedRows;";
    }
}

