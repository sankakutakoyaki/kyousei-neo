package com.kyouseipro.neo.query.sql.personnel;

import com.kyouseipro.neo.common.Utilities;

public class EmployeeSqlBuilder {

    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  employee_id INT, company_id INT, office_id INT, account NVARCHAR(255), code NVARCHAR(255), category NVARCHAR(255), " +
            "  last_name NVARCHAR(255), first_name NVARCHAR(255), last_name_kana NVARCHAR(255), first_name_kana NVARCHAR(255), " +
            "  phone_number NVARCHAR(255), postal_code NVARCHAR(255), full_address NVARCHAR(255), email NVARCHAR(255), " +
            "  gender NVARCHAR(255), blood_type NVARCHAR(255), birthday DATE, emergency_contact NVARCHAR(255), " +
            "  emergency_contact_number NVARCHAR(255), date_of_hire DATE, version INT, state INT" +
            "); ";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
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

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.employee_id, INSERTED.company_id, INSERTED.office_id, INSERTED.account, INSERTED.code, INSERTED.category, " +
            "  INSERTED.last_name, INSERTED.first_name, INSERTED.last_name_kana, INSERTED.first_name_kana, " +
            "  INSERTED.phone_number, INSERTED.postal_code, INSERTED.full_address, INSERTED.email, " +
            "  INSERTED.gender, INSERTED.blood_type, INSERTED.birthday, INSERTED.emergency_contact, " +
            "  INSERTED.emergency_contact_number, INSERTED.date_of_hire, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsert() {
        return
            buildLogTable("@InsertedRows") +

            "INSERT INTO employees (" +
            "  company_id, office_id, account, code, category, last_name, first_name, last_name_kana, first_name_kana, " +
            "  phone_number, postal_code, full_address, email, gender, blood_type, birthday, emergency_contact, " +
            "  emergency_contact_number, date_of_hire, version, state" +
            ") " +

            buildOutputLog() + "INTO @InsertedRows " +

            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLog("@InsertedRows", "INSERT") +

            "SELECT employee_id FROM @InsertedRows;";
    }

    public static String buildUpdate() {
        return
            buildLogTable("@UpdatedRows") +

            "UPDATE employees SET " +
            "  company_id=?, office_id=?, account=?, code=?, category=?, last_name=?, first_name=?, " +
            "  last_name_kana=?, first_name_kana=?, phone_number=?, postal_code=?, full_address=?, email=?, gender=?, " +
            "  blood_type=?, birthday=?, emergency_contact=?, emergency_contact_number=?, date_of_hire=?, version=?, state=? " +
            
            buildOutputLog() + "INTO @UpdatedRows " +

            "WHERE employee_id=? AND version=?; " +

            buildInsertLog("@UpdatedRows", "UPDATE") +

            "SELECT employee_id FROM @UpdatedRows;";
    }

    public static String buildDeleteByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"

        return
            buildLogTable("@DeletedRows") +

            "UPDATE employees SET state = ? " +

            buildOutputLog() + "INTO @DeletedRows " +
            
            "WHERE employee_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLog("@DeletedRows", "DELETE") +

            "SELECT employee_id FROM @DeletedRows;";
    }

    private static String baseSelectString() {
        return
            "SELECT e.employee_id, e.account, e.code, e.category" +
            ", e.first_name, e.last_name, e.full_name, e.first_name_kana, e.last_name_kana, e.full_name_kana" +
            ", e.phone_number, e.postal_code, e.full_address, e.company_id, e.office_id" +
            ", COALESCE(c.name, '') as company_name, COALESCE(o.name, '') as office_name" +
            ", e.email, e.gender, e.blood_type, e.birthday, e.emergency_contact, e.emergency_contact_number, e.date_of_hire" +
            ", e.version, e.state FROM employees e" +
            " LEFT OUTER JOIN companies c ON c.company_id = e.company_id AND NOT (c.state = ?)" +
            " LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = ?)";
    }

    public static String buildFindById() {
        return baseSelectString() + " WHERE e.employee_id = ? AND NOT (e.state = ?)";
    }

    public static String buildFindByCode() {
        return baseSelectString() + " WHERE e.code = ? AND NOT (e.state = ?)";
    }

    public static String buildFindByAccount() {
        return baseSelectString() + " WHERE e.account = ? AND NOT (e.state = ?)";
    }

    public static String buildFindAll() {
        return baseSelectString() + " WHERE NOT (e.state = ?)";
    }

    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return "SELECT * FROM employees WHERE employee_id IN (" + placeholders + ") AND NOT (state = ?)";
    }
}
