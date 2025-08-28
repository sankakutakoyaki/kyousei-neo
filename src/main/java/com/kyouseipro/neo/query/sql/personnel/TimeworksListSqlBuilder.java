package com.kyouseipro.neo.query.sql.personnel;

public class TimeworksListSqlBuilder {

    private static String baseSelectString() {
        return
            "SELECT t.timeworks_id, t.employee_id, t.category, t.work_date, t.start_time, t.end_time, t.version, t.state" +
            ", COALESCE(e.full_name, '') as full_name, COALESCE(o.name, '') as office_name FROM timeworks t" +
            " LEFT OUTER JOIN employees e ON e.employee_id = t.employee_id AND NOT (e.state = ?)" +
            " LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = ?)";
    }

    public static String buildFindByTodaysEntityByEmployeeId() {
        return baseSelectString() +  " WHERE t.employee_id = ? AND NOT (t.state = ?) AND t.work_date = ?";
    }

    public static String buildFindByDateSql() {
        return baseSelectString() + " WHERE t.work_date = ? AND NOT (t.state = ?)";
    }

    private static String buildLogTableSql(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  timeworks_id INT, employee_id INT, category INT, " +
            "  work_date DATE, start_time TIME, end_time TIME, version INT, state INT" +
            "); ";
    }

    private static String buildInsertLogSql(String rowTableName, String processName) {
        return
            "INSERT INTO timeworks_log (" +
            "timeworks_id, editor, process, log_date, employee_id, category, work_date, start_time, end_time, version, state" +
            ") " +
            "SELECT timeworks_id, ?, '" + processName + "', CURRENT_TIMESTAMP, employee_id, category, work_date, start_time, end_time, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.timeworks_id, INSERTED.employee_id, INSERTED.category, " +
            "INSERTED.work_date, INSERTED.start_time, INSERTED.end_time, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsertSql() {
        return
            buildLogTableSql("@Inserted") +

            "INSERT INTO timeworks (" +
            "  employee_id, category, work_date, start_time, end_time, version, state" +
            ") " +
            buildOutputLogSql() + "INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLogSql("@Inserted", "INSERT") +
            "SELECT timeworks_id FROM @Inserted;";
    }

    public static String buildUpdateSql() {
        return
            buildLogTableSql("@Updated") +

            "UPDATE timeworks SET " +
            "  employee_id=?, category=?, work_date=?, start_time=?, end_time=?, version=?, state=? " +
            buildOutputLogSql() + "INTO @Updated " +
            "WHERE timeworks_id=?; " +

            buildInsertLogSql("@Updated", "UPDATE") +
            "SELECT timeworks_id FROM @Updated;";
    }
}
