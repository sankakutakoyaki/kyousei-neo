package com.kyouseipro.neo.personnel.paidholiday.repository;

import com.kyouseipro.neo.common.Utilities;

public class PaidHolidaySqlBuilder {
    // 有給
    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "paid_holiday_id INT, employee_id INT, " +
            "start_date DATE, end_date DATE, permit_employee_id INT, reason NVARCHAR(255), " +
            "version INT, state INT" +
            "); ";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
        return
            "INSERT INTO paid_holiday_log (" +
            "paid_holiday_id, editor, process, log_date, employee_id, start_date, end_date, " +
            "permit_employee_id, reason, version, state" +
            ") " +
            "SELECT paid_holiday_id, ?, '" + processName + "', CURRENT_TIMESTAMP, employee_id, start_date, end_date" +
            ", permit_employee_id, reason, version, state " +
            "FROM " + rowTableName + ";";
    }

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.paid_holiday_id, INSERTED.employee_id, " +
            "INSERTED.start_date, INSERTED.end_date, INSERTED.permit_employee_id, INSERTED.reason," +
            "INSERTED.version, INSERTED.state ";
    }

    public static String buildInsert() {
        return
            buildLogTable("@Inserted") +

            "INSERT INTO paid_holiday (" +
            "employee_id, start_date, end_date, permit_employee_id, reason, version, state" +
            ") " +
            buildOutputLog() + "INTO @Inserted " +
            "SELECT ?, ?, ?, ?, ?, ?, ? " + 
            "WHERE NOT EXISTS ( " +
            " SELECT 1 FROM paid_holiday " +
            " WHERE employee_id=? " +
            " AND NOT (state=?) " +
            " AND start_date <=? " +
            " AND end_date >=? )" +
            

            buildInsertLog("@Inserted", "INSERT") +
            "SELECT paid_holiday_id FROM @Inserted;";
    }

    public static String buildDelete() {
        return
            buildLogTable("@Deleted") +

            "UPDATE paid_holiday SET state=? " +
            buildOutputLog() + "INTO @Deleted " +
            "WHERE paid_holiday_id=?; " +

            buildInsertLog("@Deleted", "UPDATE") +
            "SELECT paid_holiday_id FROM @Deleted;";
    }

    public static String baseSelectJoinStringNotDelete(int count) {
        return baseSelectJoinString(count, "delete");
    }

    public static String baseSelectJoinStringForComplete(int count) {
        return baseSelectJoinString(count, "complete");
    }

    private static String baseSelectJoinString(int count, String flag) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        String str =
            // -- 再帰CTEで有給期間を日単位で展開
            "WITH HolidayDates AS ( " +
            "    SELECT " +
            "        ph.employee_id, " +
            "        CAST(ph.start_date AS DATE) AS holiday_date, " +
            "        CAST(ph.end_date AS DATE) AS end_date, " +
            "        ph.paid_holiday_id " +
            "    FROM paid_holiday ph " +
            "    WHERE ph.state <> ? " +

            "    UNION ALL " +

            "    SELECT " +
            "        hd.employee_id, " +
            "        DATEADD(DAY, 1, hd.holiday_date), " +
            "        hd.end_date, " +
            "        hd.paid_holiday_id " +
            "    FROM HolidayDates hd " +
            "    WHERE DATEADD(DAY, 1, hd.holiday_date) <= hd.end_date " +
            ") " +

            // -- 勤務 + 有給をまとめて取得
            "SELECT " +
            "    t.timeworks_id, " +
            "    t.employee_id, " +
            "    t.work_date, " +
            "    t.start_time, " +
            "    t.end_time, " +
            "    t.comp_start_time, " +
            "    t.comp_end_time, " +
            "    t.rest_time, " +
            "    CAST('1900-01-01 00:00:00' AS DATETIME) AS basic_start_time, " +
            "    CAST('1900-01-01 00:00:00' AS DATETIME) AS basic_end_time, " +
            "    t.category, " +
            "    t.version, " +
            "    t.state, " +
            "    COALESCE(e.full_name, '') AS full_name, " +
            "    COALESCE(o.name, '') AS office_name, " +
            "    N'勤務' AS situation " +
            "FROM timeworks t " +
            "LEFT JOIN employees e " +
            "    ON e.employee_id = t.employee_id " +
            "   AND e.state <> ? " +
            "LEFT JOIN offices o " +
            "    ON o.office_id = e.office_id " +
            "   AND o.state <> ? " +
            "WHERE ";
        switch (flag) {
            case "delete":
                str += "t.state <> ? ";
                break;
            case "complete":
                str += "t.state = ? ";
                break;
            default:
                str += "1=? ";
                break;
        };
        str +=
            "AND t.employee_id IN (" + placeholders + ") " +
            "AND t.work_date >= ? " +
            "AND t.work_date < ? " +

            "UNION ALL " +

            "SELECT " +
            "    0 AS timeworks_id, " +
            "    hd.employee_id, " +
            "    hd.holiday_date AS work_date, " +
            "    CAST('1900-01-01 00:00:00' AS DATETIME) AS start_time, " +
            "    CAST('1900-01-01 00:00:00' AS DATETIME) AS end_time, " +
            "    CAST('1900-01-01 00:00:00' AS DATETIME) AS comp_start_time, " +
            "    CAST('1900-01-01 00:00:00' AS DATETIME) AS comp_end_time, " +
            "    CAST('1900-01-01 00:00:00' AS DATETIME) AS rest_time, " +
            "    CAST('1900-01-01 00:00:00' AS DATETIME) AS basic_start_time, " +
            "    CAST('1900-01-01 00:00:00' AS DATETIME) AS basic_end_time, " +
            "    0 AS category, " +
            "    0 AS version, " +
            "    0 AS state, " +
            "    COALESCE(em.full_name, '') AS full_name, " +
            "    COALESCE(om.name, '') AS office_name, " +
            "    N'有給' AS situation " +
            "FROM HolidayDates hd " +
            "LEFT JOIN employees em " +
            "    ON em.employee_id = hd.employee_id " +
            "   AND em.state <> ? " +
            "LEFT JOIN offices om " +
            "    ON om.office_id = em.office_id " +
            "   AND om.state <> ? " +
            "WHERE hd.employee_id IN (" + placeholders +") " +
            "    AND hd.holiday_date >= ? " +
            "    AND hd.holiday_date <= ? " +   
            "AND NOT EXISTS ( " +
            "    SELECT 1 " +
            "    FROM timeworks t " +
            "    WHERE t.employee_id = hd.employee_id " +
            "        AND t.work_date = hd.holiday_date " +
            ") " +
            "ORDER BY employee_id, work_date;";

        return str;
    }

    // 有給休暇取得リスト
    public static String buildFindByOfficeIdFromYear() {
        return 
        "SELECT e.employee_id, COALESCE(e.full_name, '') AS full_name, " +
        "COALESCE(SUM(DATEDIFF(DAY, p.start_date, p.end_date) + 1), 0) AS total_days " +
        "FROM employees e " +
        "LEFT JOIN paid_holiday p ON p.employee_id = e.employee_id AND NOT (p.state = ?) " +
        "AND p.start_date >= CAST(? + '-01-01' AS DATE) AND p.end_date <= CAST(? + '-12-31' AS DATE) " +
        "WHERE NOT (e.state = ?) AND e.office_id = ? GROUP BY e.employee_id, e.full_name ORDER BY e.employee_id;";
    }

    // 有給休暇取得リスト
    private static String baseSelectString() {
        return
            "SELECT p.paid_holiday_id, p.employee_id" +
            ", p.start_date, p.end_date, p.permit_employee_id, p.reason, p.version, p.state" +
            ", COALESCE(e.full_name, '') as full_name FROM paid_holiday p" +
            " LEFT OUTER JOIN employees e ON e.employee_id = p.employee_id AND NOT (e.state = ?)";
    }

    public static String buildFindByEmployeeIdFromYear() {
        return baseSelectString() +  " WHERE p.employee_id = ? AND NOT (p.state = ?) " +
        "AND p.start_date >= CAST(? + '-01-01' AS DATE) AND p.end_date <= CAST(? + '-12-31' AS DATE) ORDER BY p.start_date;";
    }
}
