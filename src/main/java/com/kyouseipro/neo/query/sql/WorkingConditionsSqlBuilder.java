package com.kyouseipro.neo.query.sql;

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
}
