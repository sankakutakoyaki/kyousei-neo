package com.kyouseipro.neo.query.sql.personnel;

import com.kyouseipro.neo.common.Utilities;

public class EmployeeSqlBuilder {

    private static String insertLogSql() {
        return
            "INSERT INTO employees_log (employee_id, editor, process, log_date, " +
            "company_id, office_id, account, code, category, last_name, first_name, last_name_kana, first_name_kana, " +
            "phone_number, postal_code, full_address, email, gender, blood_type, birthday, emergency_contact, emergency_contact_number, date_of_hire, version, state) " +
            "SELECT e.employee_id, ?, 'INSERT', CURRENT_TIMESTAMP, " +
            "e.company_id, e.office_id, e.account, e.code, e.category, e.last_name, e.first_name, e.last_name_kana, e.first_name_kana, " +
            "e.phone_number, e.postal_code, e.full_address, e.email, e.gender, e.blood_type, e.birthday, e.emergency_contact, e.emergency_contact_number, e.date_of_hire, e.version, e.state " +
            "FROM employees e ";
    }

    public static String buildInsertEmployeeSql() {
        return
            "DECLARE @InsertedRows TABLE (employee_id INT); " +
            "INSERT INTO employees (company_id, office_id, account, code, category, " +
            "last_name, first_name, last_name_kana, first_name_kana, " +
            "phone_number, postal_code, full_address, email, gender, blood_type, birthday, emergency_contact, " +
            "emergency_contact_number, date_of_hire, version, state) " +
            "OUTPUT INSERTED.employee_id INTO @InsertedRows " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +
            insertLogSql() +
            "INNER JOIN @InsertedRows ir ON e.employee_id = ir.employee_id; " +
            "SELECT employee_id FROM @InsertedRows;";
    }

    public static String buildUpdateEmployeeSql() {
        return
            "DECLARE @UpdatedRows TABLE (employee_id INT); " +
            "UPDATE employees SET " +
            "company_id=?, office_id=?, account=?, code=?, category=?, " +
            "last_name=?, first_name=?, last_name_kana=?, first_name_kana=?, " +
            "phone_number=?, postal_code=?, full_address=?, email=?, gender=?, blood_type=?, birthday=?, emergency_contact=?, " +
            "emergency_contact_number=?, date_of_hire=?, version=?, state=? " +
            "OUTPUT INSERTED.employee_id INTO @UpdatedRows " +
            "WHERE employee_id=?; " +
            insertLogSql() +
            "INNER JOIN @UpdatedRows ur ON e.employee_id = ur.employee_id; " +
            "SELECT employee_id FROM @UpdatedRows;";
    }

    public static String buildDeleteEmployeeForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return
            "DECLARE @DeletedRows TABLE (employee_id INT); " +
            "UPDATE employees " +
            "SET state = ? " +
            "OUTPUT INSERTED.employee_id INTO @DeletedRows " +
            "WHERE employee_id IN (" + placeholders + ") " +
            "AND NOT (state = ?); " +
            insertLogSql() +
            "INNER JOIN @DeletedRows dr ON e.employee_id = dr.employee_id; " +
            "SELECT employee_id FROM @DeletedRows;";
    }

    public static String buildFindByIdSql() {
        return "SELECT * FROM employees WHERE employee_id = ? AND NOT (state = ?)";
    }

    public static String buildFindByAccountSql() {
        return "SELECT * FROM employees WHERE account = ? AND NOT (state = ?)";
    }

    public static String buildFindAllSql() {
        return "SELECT * FROM employees WHERE NOT (state = ?)";
    }

    public static String buildDownloadCsvEmployeeForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return "SELECT * FROM employees WHERE employee_id IN (" + placeholders + ") \" + NOT (state = ?)";
    }
}
