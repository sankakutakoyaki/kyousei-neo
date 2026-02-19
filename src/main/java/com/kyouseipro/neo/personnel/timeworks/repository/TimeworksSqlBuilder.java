package com.kyouseipro.neo.personnel.timeworks.repository;

public class TimeworksSqlBuilder {
    
    private static String baseSelectString() {
        return """
            SELECT
                t.timeworks_id,
                t.employee_id,
                t.start_dt,
                t.end_dt,
                t.break_minutes,
                t.work_base_date,
                t.start_latitude,
                t.start_longitude,
                t.end_latitude,
                t.end_longitude,
                t.work_type,
                t.state,
                COALESCE(e.full_name, '') AS full_name,
                COALESCE(o.name, '') AS office_name
            FROM timeworks t
            LEFT OUTER JOIN employees e
                ON e.employee_id = t.employee_id
            LEFT OUTER JOIN offices o
                ON o.office_id = e.office_id
            """;
    }

    /**
     * ID指定した従業員の今日の勤怠データを取得する
     * @return
     */
    public static String buildFindToday() {
        return baseSelectString() + """
            WHERE t.employee_id = ? AND t.state <> 9
            AND t.work_base_date = ?
            """;
    }

    /**
     * 全従業員の今日の勤怠データを取得する
     * @return
     */
    public static String buildFindAllByBaseDate() {
        return baseSelectString() + """
            WHERE t.work_base_date = ?
            ORDER BY employee_id
            """;
    }
}
