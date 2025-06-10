package com.kyouseipro.neo.query.sql.personnel;

import com.kyouseipro.neo.common.Utilities;

public class EmployeeSqlBuilder {

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  employee_id INT, company_id INT, office_id INT, account NVARCHAR(255), code NVARCHAR(255), category NVARCHAR(255), " +
            "  last_name NVARCHAR(255), first_name NVARCHAR(255), last_name_kana NVARCHAR(255), first_name_kana NVARCHAR(255), " +
            "  phone_number NVARCHAR(255), postal_code NVARCHAR(255), full_address NVARCHAR(255), email NVARCHAR(255), " +
            "  gender NVARCHAR(255), blood_type NVARCHAR(255), birthday DATE, emergency_contact NVARCHAR(255), " +
            "  emergency_contact_number NVARCHAR(255), date_of_hire DATE, version INT, state INT" +
            "); ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO employees_log (" +
            "  employee_id, editor, process, log_date, company_id, office_id, account, code, category, " +
            "  last_name, first_name, last_name_kana, first_name_kana, phone_number, postal_code, full_address, " +
            "  email, gender, blood_type, birthday, emergency_contact, emergency_contact_number, date_of_hire, version, state" +
            ") " +
            "SELECT employee_id, ?, '" + processName + "', CURRENT_TIMESTAMP, company_id, office_id, account, code, category, " +
            "  last_name, first_name, last_name_kana, first_name_kana, phone_number, postal_code, full_address, " +
            "  email, gender, blood_type, birthday, emergency_contact, emergency_contact_number, date_of_hire, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.employee_id, INSERTED.company_id, INSERTED.office_id, INSERTED.account, INSERTED.code, INSERTED.category, " +
            "  INSERTED.last_name, INSERTED.first_name, INSERTED.last_name_kana, INSERTED.first_name_kana, " +
            "  INSERTED.phone_number, INSERTED.postal_code, INSERTED.full_address, INSERTED.email, " +
            "  INSERTED.gender, INSERTED.blood_type, INSERTED.birthday, INSERTED.emergency_contact, " +
            "  INSERTED.emergency_contact_number, INSERTED.date_of_hire, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertEmployeeSql() {
        return
            buildLogTableSql("@InsertedRows") +

            "INSERT INTO employees (" +
            "  company_id, office_id, account, code, category, last_name, first_name, last_name_kana, first_name_kana, " +
            "  phone_number, postal_code, full_address, email, gender, blood_type, birthday, emergency_contact, " +
            "  emergency_contact_number, date_of_hire, version, state" +
            ") " +

            buildOutputLogSql() + "INTO @InsertedRows " +

            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql("@InsertedRows", "INSERT") +

            "SELECT employee_id FROM @InsertedRows;";
    }

    public static String buildUpdateEmployeeSql() {
        return
            buildLogTableSql("@UpdatedRows") +

            "UPDATE employees SET " +
            "  company_id=?, office_id=?, account=?, code=?, category=?, last_name=?, first_name=?, " +
            "  last_name_kana=?, first_name_kana=?, phone_number=?, postal_code=?, full_address=?, email=?, gender=?, " +
            "  blood_type=?, birthday=?, emergency_contact=?, emergency_contact_number=?, date_of_hire=?, version=?, state=? " +
            
            buildOutputLogSql() + "INTO @UpdatedRows " +

            "WHERE employee_id=?; " +

            buildInsertLogSql("@UpdatedRows", "UPDATE") +

            "SELECT employee_id FROM @UpdatedRows;";
    }

    public static String buildDeleteEmployeeForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"

        return
            buildLogTableSql("@DeletedRows") +

            "UPDATE employees SET state = ? " +

            buildOutputLogSql() + "INTO @DeletedRows " +
            
            "WHERE employee_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLogSql("@DeletedRows", "DELETE") +

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
        return "SELECT * FROM employees WHERE employee_id IN (" + placeholders + ") AND NOT (state = ?)";
    }
}
