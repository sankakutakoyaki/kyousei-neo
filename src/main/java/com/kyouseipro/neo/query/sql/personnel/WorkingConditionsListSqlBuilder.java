package com.kyouseipro.neo.query.sql.personnel;

public class WorkingConditionsListSqlBuilder {
    
    public static String buildFindAllSql() {
        // return "SELECT * FROM working_conditions WHERE NOT (state = ?)";
        return "SELECT e.employee_id, e.full_name, e.full_name_kana, e.category, e.state, w.working_conditions_id "+
        ", ISNULL(NULLIF(o.name, ''), '登録なし') as office_name FROM employees e" +
        " LEFT OUTER JOIN working_conditions w ON w.employee_id = e.employee_id AND NOT (w.state = ?)" +
        " LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = ?)" +
        " WHERE e.category = ?;";
    }

    public static String buildFindAllByCategoryIdSql() {
        return "SELECT e.employee_id, e.code, e.full_name, e.full_name_kana, e.category, e.phone_number" +
            ", COALESCE(c.name, '') as company_name, COALESCE(o.name, '') as office_name FROM employees e" +
            ", COALESCE(c.name_kana, '') as company_name_kana, COALESCE(o.name_kana, '') as office_name_kana FROM employees e" +
            " LEFT OUTER JOIN companies c ON c.company_id = e.company_id AND NOT (c.state = ?)" +
            " LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = ?)" +
            " WHERE NOT (e.state = ?) AND e.category = ?;";
    }
}
