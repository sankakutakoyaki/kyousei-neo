package com.kyouseipro.neo.query.sql.personnel;

import com.kyouseipro.neo.common.Utilities;

public class WorkingConditionsSqlBuilder {

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  working_conditions_id INT, employee_id INT, code NVARCHAR(255), category NVARCHAR(255), " +
            "  payment_method NVARCHAR(255), pay_type NVARCHAR(255), base_salary INT, trans_cost INT, " +
            "  basic_start_time TIME, basic_end_time TIME, version INT, state INT" +
            "); ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO working_conditions_log (" +
            "  working_conditions_id, editor, process, log_date, employee_id, code, category, payment_method, " +
            "  pay_type, base_salary, trans_cost, basic_start_time, basic_end_time, version, state" +
            ") " +
            "SELECT working_conditions_id, ?, '" + processName + "', CURRENT_TIMESTAMP, employee_id, code, category, payment_method, " +
            "  pay_type, base_salary, trans_cost, basic_start_time, basic_end_time, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.working_conditions_id, INSERTED.employee_id, INSERTED.code, INSERTED.category, " +
            "  INSERTED.payment_method, INSERTED.pay_type, INSERTED.base_salary, INSERTED.trans_cost, " +
            "  INSERTED.basic_start_time, INSERTED.basic_end_time, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertSql() {
        return
            buildLogTableSql("@Inserted") +

            "INSERT INTO working_conditions (" +
            "  employee_id, code, category, payment_method, pay_type, base_salary, trans_cost, " +
            "  basic_start_time, basic_end_time, version, state" +
            ") " +
            buildOutputLogSql() + "INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql("@Inserted", "INSERT") +
            "SELECT working_conditions_id FROM @Inserted;";
    }

    public static String buildUpdateSql() {
        return
            buildLogTableSql("@Updated") +

            "UPDATE working_conditions SET " +
            "  employee_id=?, code=?, category=?, payment_method=?, pay_type=?, base_salary=?, trans_cost=?, " +
            "  basic_start_time=?, basic_end_time=?, version=?, state=? " +
            buildOutputLogSql() + "INTO @Updated " +
            "WHERE working_conditions_id=?; " +

            buildInsertLogSql("@Updated", "UPDATE") +
            "SELECT working_conditions_id FROM @Updated;";
    }

    public static String buildDeleteWorkingConditionsForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count);

        return
            buildLogTableSql("@Deleted") +

            "UPDATE working_conditions SET state = ? " +
            buildOutputLogSql() + "INTO @Deleted " +
            "WHERE working_conditions_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLogSql("@Deleted", "DELETE") +
            "SELECT working_conditions_id FROM @Deleted;";
    }

    public static String buildDeleteWorkingConditionsSql() {
        return
            buildLogTableSql("@Deleted") +

            "UPDATE working_conditions SET state = ? " +
            buildOutputLogSql() + "INTO @Deleted " +
            "WHERE working_conditions_id = ?; " +

            buildInsertLogSql("@Deleted", "DELETE") +
            "SELECT working_conditions_id FROM @Deleted;";
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

    public static String buildDownloadCsvWorkingConditionsForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return
            basicSelectString() + 
            " WHERE working_conditions_id IN (" + placeholders + ") \" + NOT (state = ?)";
    }
}
