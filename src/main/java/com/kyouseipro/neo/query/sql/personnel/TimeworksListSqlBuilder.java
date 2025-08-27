package com.kyouseipro.neo.query.sql.personnel;

public class TimeworksListSqlBuilder {

    public static String buildFindByIdSql() {
        return "SELECT * FROM timeworks WHERE employee_id = ? AND NOT (state = ?)";
    }

    public static String buildFindByDateSql() {
        return "SELECT * FROM timeworks WHERE work_date = ? AND NOT (state = ?)";
    }
}
