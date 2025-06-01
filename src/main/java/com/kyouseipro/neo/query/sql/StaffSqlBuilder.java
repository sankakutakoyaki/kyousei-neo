package com.kyouseipro.neo.query.sql;

public class StaffSqlBuilder {

    public static String buildInsertStaffSql() {
        return
            "DECLARE @InsertedRows TABLE (staff_id INT);" +
            "INSERT INTO staffs (company_id, office_id, company_name, office_name, name, name_kana, phone_number, email, version, state) " +
            "OUTPUT INSERTED.staff_id INTO @InsertedRows " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);" +
            "INSERT INTO staffs_log (staff_id, editor, process, log_date, company_id, office_id, company_name, office_name, name, name_kana, phone_number, email, version, state) " +
            "SELECT s.staff_id, ?, 'INSERT', CURRENT_TIMESTAMP, s.company_id, s.office_id, s.company_name, s.office_name, s.name, s.name_kana, s.phone_number, s.email, s.version, s.state " +
            "FROM staffs s INNER JOIN @InsertedRows ir ON s.staff_id = ir.staff_id;" +
            "SELECT staff_id FROM @InsertedRows;";
    }

    public static String buildUpdateStaffSql() {
        return
            "DECLARE @UpdatedRows TABLE (staff_id INT);" +
            "UPDATE staffs SET company_id=?, office_id=?, company_name=?, office_name=?, name=?, name_kana=?, phone_number=?, email=?, version=?, state=? " +
            "OUTPUT INSERTED.staff_id INTO @UpdatedRows " +
            "WHERE staff_id=?;" +
            "INSERT INTO staffs_log (staff_id, editor, process, log_date, company_id, office_id, company_name, office_name, name, name_kana, phone_number, email, version, state) " +
            "SELECT s.staff_id, ?, 'UPDATE', CURRENT_TIMESTAMP, s.company_id, s.office_id, s.company_name, s.office_name, s.name, s.name_kana, s.phone_number, s.email, s.version, s.state " +
            "FROM staffs s INNER JOIN @UpdatedRows ur ON s.staff_id = ur.staff_id;" +
            "SELECT staff_id FROM @UpdatedRows;";
    }
}
