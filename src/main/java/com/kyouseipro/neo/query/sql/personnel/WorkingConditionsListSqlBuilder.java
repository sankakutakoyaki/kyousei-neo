package com.kyouseipro.neo.query.sql.personnel;

public class WorkingConditionsListSqlBuilder {
    
    private static String basicSelectString() {
        return
            "SELECT w.working_conditions_id, e.employee_id, e.category, e.full_name, e.full_name_kana"+
            ", ISNULL(NULLIF(o.name, ''), '登録なし') as office_name FROM employees e" +
            " LEFT OUTER JOIN working_conditions w ON w.employee_id = e.employee_id AND NOT (w.state = ?)" +
            " LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = ?)";
    }
    public static String buildFindAllSql() {
        // return "SELECT * FROM working_conditions WHERE NOT (state = ?)";
        return 
            basicSelectString() +
            " WHERE NOT (e.state = ?)";
    }

    public static String buildFindAllByCategoryIdSql() {
        return 
            basicSelectString() +
            " WHERE NOT (e.state = ?) AND e.category = ?;";
    }
}
