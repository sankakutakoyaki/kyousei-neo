package com.kyouseipro.neo.query.sql.personnel;

import com.kyouseipro.neo.common.Utilities;

public class WorkingConditionsSqlBuilder {

    public static String buildInsertSql() {
        return
            "DECLARE @Inserted TABLE (working_conditions_id INT);" +
            "INSERT INTO working_conditions (employee_id, code, category, payment_method, pay_type, base_salary, trans_cost, basic_start_time, basic_end_time, version, state) " +
            "OUTPUT INSERTED.working_conditions_id INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);" +
            "INSERT INTO working_conditions_log (working_conditions_id, editor, process, log_date, employee_id, code, category, payment_method, pay_type, base_salary, trans_cost, basic_start_time, basic_end_time, version, state) " +
            "SELECT w.working_conditions_id, ?, 'INSERT', CURRENT_TIMESTAMP, w.employee_id, w.code, w.category, w.payment_method, w.pay_type, w.base_salary, w.trans_cost, w.basic_start_time, w.basic_end_time, w.version, w.state " +
            "FROM working_conditions w INNER JOIN @Inserted i ON w.working_conditions_id = i.working_conditions_id;" +
            "SELECT working_conditions_id FROM @Inserted;";
    }

    public static String buildUpdateSql() {
        return
            "DECLARE @Updated TABLE (working_conditions_id INT);" +
            "UPDATE working_conditions SET employee_id=?, code=?, category=?, payment_method=?, pay_type=?, base_salary=?, trans_cost=?, basic_start_time=?, basic_end_time=?, version=?, state=? " +
            "OUTPUT INSERTED.working_conditions_id INTO @Updated " +
            "WHERE working_conditions_id=?;" +
            "INSERT INTO working_conditions_log (working_conditions_id, editor, process, log_date, employee_id, code, category, payment_method, pay_type, base_salary, trans_cost, basic_start_time, basic_end_time, version, state) " +
            "SELECT w.working_conditions_id, ?, 'UPDATE', CURRENT_TIMESTAMP, w.employee_id, w.code, w.category, w.payment_method, w.pay_type, w.base_salary, w.trans_cost, w.basic_start_time, w.basic_end_time, w.version, w.state " +
            "FROM working_conditions w INNER JOIN @Updated u ON w.working_conditions_id = u.working_conditions_id;" +
            "SELECT working_conditions_id FROM @Updated;";
    }

    private static String basicSelectString() {
        return
            "SELECT e.employee_id, e.full_name, e.full_name_kana, e.code, e.category" +
            ", w.working_conditions_id, w.payment_method, w.pay_type, w.base_salary, w.trans_cost, w.version, w.state" +
            ", ISNULL(NULLIF(w.basic_start_time, ''), '00:00:00') as basic_start_time, ISNULL(NULLIF(w.basic_end_time, ''), '00:00:00') as basic_end_time" +
            ", ISNULL(NULLIF(o.name, ''), '登録なし') as office_name FROM employees e" +
            " LEFT OUTER JOIN working_conditions w ON w.employee_id = e.employee_id AND NOT (w.state = ?)" +
            " LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = ?)";
    }
    public static String buildFindByIdSql() {
        return
            basicSelectString() +
            " WHERE NOT (e.state = ?) AND w.working_conditions_id = ?";
    }

    public static String buildFindAllSql() {
        return
            basicSelectString() +
            " WHERE NOT (e.state = ?)";
    }

    public static String buildDeleteWorkingConditionsForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return
            "DECLARE @DeletedRows TABLE (working_conditions_id INT); " +
            "UPDATE working_conditions " +
            "SET state = ? " +
            "OUTPUT INSERTED.working_conditions_id INTO @DeletedRows " +
            "WHERE working_conditions_id IN (" + placeholders + ") " +
            "AND NOT (state = ?); " +
            "INSERT INTO working_conditions_log (working_conditions_id, editor, process, log_date, employee_id, code, category, payment_method, pay_type, base_salary, trans_cost, basic_start_time, basic_end_time, version, state) " +
            "SELECT w.working_conditions_id, ?, 'UPDATE', CURRENT_TIMESTAMP, w.employee_id, w.code, w.category, w.payment_method, w.pay_type, w.base_salary, w.trans_cost, w.basic_start_time, w.basic_end_time, w.version, w.state " +
            "FROM working_conditions w INNER JOIN @Updated u ON w.working_conditions_id = u.working_conditions_id;" +
            "SELECT working_conditions_id FROM @Updated;";
    }

    public static String buildDownloadCsvWorkingConditionsForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return
            basicSelectString() +
            " WHERE working_conditions_id IN (" + placeholders + ") \" + NOT (state = ?)";
    }
}
