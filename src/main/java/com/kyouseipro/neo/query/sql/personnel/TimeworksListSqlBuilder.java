package com.kyouseipro.neo.query.sql.personnel;

public class TimeworksListSqlBuilder {

    public static String buildFindByTodaysEntityByEmployeeId() {
        return "SELECT * FROM timeworks WHERE employee_id = ? AND NOT (state = ?) AND work_date = ?";
    }

    public static String buildFindByDateSql() {
        return "SELECT * FROM timeworks WHERE work_date = ? AND NOT (state = ?)";
    }
}
