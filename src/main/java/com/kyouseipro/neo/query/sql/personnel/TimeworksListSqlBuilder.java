package com.kyouseipro.neo.query.sql.personnel;

public class TimeworksListSqlBuilder {

    private static String baseSelectString() {
        return
            "SELECT t.timeworks_id, t.employee_id, t.category" +
            ", t.work_date, t.start_date_time, t.start_time, t.end_date_time, t.end_time, t.comp_start_time, t.comp_end_time, t.rest_time, t.version, t.state" +
            ", COALESCE(w.basic_start_time, '00:00:00') as basic_start_time, COALESCE(w.basic_end_time, '00:00:00') as basic_end_time" +
            ", COALESCE(e.full_name, '') as full_name, COALESCE(o.name, '') as office_name, '' as situation FROM timeworks t" +
            " LEFT OUTER JOIN employees e ON e.employee_id = t.employee_id AND NOT (e.state = ?)" +
            " LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = ?)" +
            " LEFT OUTER JOIN working_conditions w ON t.employee_id = w.employee_id AND NOT (w.state = ?)";
    }

    public static String buildFindByTodaysByEmployeeId() {
        return baseSelectString() +  " WHERE t.employee_id = ? AND NOT (t.state = ?) AND t.work_date = ?";
    }

    public static String buildFindByBetweenByEmployeeId() {
        return baseSelectString() +  " WHERE t.employee_id = ? AND t.state = ? AND t.work_date BETWEEN ? AND ?";
    }

    public static String buildFindAllByBetweenByEmployeeId() {
        return PaidHolidaySqlBuilder.baseSelectJoinStringNotDelete(1);
    }

    private static String baseBetweenString() {
        return
            "SELECT t.employee_id, COALESCE(e.full_name, '') as full_name, COUNT(DISTINCT t.work_date) AS total_working_date, " +
            "ROUND(SUM(CASE WHEN t.comp_start_time = '00:00:00' OR t.comp_end_time = '00:00:00' THEN NULL " +
            " ELSE DATEDIFF(MINUTE, t.comp_start_time, " +
            "CASE WHEN t.comp_end_time < t.comp_start_time THEN DATEADD(DAY, 1, t.comp_end_time) " +
            "ELSE t.comp_end_time END) - DATEDIFF(MINUTE, '00:00:00', t.rest_time) END " +
            ") / 60.0, 2) AS total_working_time " +
            "FROM timeworks t " +
            "LEFT OUTER JOIN employees e ON e.employee_id = t.employee_id AND NOT (e.state = ?) ";
    }

    public static String buildFindByBetweenSummary() {
        return 
        baseBetweenString() +
        "WHERE t.state = ? AND t.work_date >= ? AND t.work_date <= ? GROUP BY t.employee_id, full_name, t.state;";
    }

    public static String buildFindByBetweenSummaryByOfficeId() {
        return 
        baseBetweenString() +
        "WHERE t.state = ? AND  e.office_id = ? AND t.work_date >= ? AND t.work_date <= ? GROUP BY t.employee_id, full_name, t.state;";
    }

    public static String buildFindByDate() {
        return baseSelectString() + " WHERE t.work_date = ? AND NOT (t.state = ?)";
    }

    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "timeworks_id INT, employee_id INT, category INT, " +
            "work_date DATE, start_date_time DATETIME, start_time TIME, end_date_time ENDTIME, end_time TIME, " +
            "comp_start_time TIME, comp_end_time TIME, rest_time TIME, version INT, state INT" +
            "); ";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
        return
            "INSERT INTO timeworks_log (" +
            "timeworks_id, editor, process, log_date, employee_id, category, work_date, " +
            "start_date_time, start_time, end_date_time, end_time, comp_start_time, comp_end_time, rest_time, version, state" +
            ") " +
            "SELECT timeworks_id, ?, '" + processName + "', CURRENT_TIMESTAMP, employee_id, category, work_date" +
            ", start_date_time, start_time, end_date_time, end_time, comp_start_time, comp_end_time, rest_time, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.timeworks_id, INSERTED.employee_id, INSERTED.category, " +
            "INSERTED.work_date, INSERTED.start_date_time, INSERTED.start_time, INSERTED.end_date_time, INSERTED.end_time, " +
            "INSERTED.comp_start_time, INSERTED.comp_end_time, INSERTED.rest_time, INSERTED.version, INSERTED.state ";
    }

    public static String buildInsert() {
        return
            buildLogTable("@Inserted") +

            // 有給チェック（存在したら -1 を返して処理中止）
            "IF EXISTS (" +
            "   SELECT 1 " +
            "   FROM paid_holiday ph " +
            "   WHERE ph.employee_id = ? " +  // 1: employee_id
            "     AND CAST(? AS DATE) BETWEEN CAST(ph.start_date AS DATE) AND CAST(ph.end_date AS DATE)" + // 2: work_date
            "     AND NOT (ph.state = ?)" + //3: state
            ") " +
            "BEGIN " +
            "   SELECT -1 AS timeworks_id; " + // -1 を返して終了
            "   RETURN; " +
            "END; " +

            // 通常の勤務 INSER
            "INSERT INTO timeworks (" +
            "  employee_id, category, work_date, start_date_time, start_time, end_date_time, end_time, comp_start_time, comp_end_time, rest_time, version, state" +
            ") " +
            buildOutputLog() + "INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

            buildInsertLog("@Inserted", "INSERT") +
            "SELECT timeworks_id FROM @Inserted;";
    }

    public static String buildUpdate() {
        return
            buildLogTable("@Updated") +

            "UPDATE timeworks SET " +
            "  employee_id=?, category=?, work_date=?, start_date_tiem=?, start_time=?, end_date_time=?, end_time=?, comp_start_time=?, comp_end_time=?, rest_time=?, version=?, state=? " +
            buildOutputLog() + "INTO @Updated " +
            "WHERE timeworks_id=?; " +

            buildInsertLog("@Updated", "UPDATE") +
            "SELECT timeworks_id FROM @Updated;";
    }

    public static String buildReverseConfirm() {
        return
            buildLogTable("@Updated") +

            "UPDATE timeworks SET " +
            " state=? " +
            buildOutputLog() + "INTO @Updated " +
            "WHERE timeworks_id=?; " +

            buildInsertLog("@Updated", "UPDATE") +
            "SELECT timeworks_id FROM @Updated;";
    }

    public static String buildDownloadCsvForIdsFromBetween(int count) {
        return
            PaidHolidaySqlBuilder.baseSelectJoinStringForComplete(count);
    }
}
