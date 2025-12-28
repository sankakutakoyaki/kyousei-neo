package com.kyouseipro.neo.query.sql.personnel;

public class EmployeeListSqlBuilder {
    
    private static String baseSelectString() {
        return
            "SELECT e.employee_id, e.code, e.full_name, e.full_name_kana, e.category, e.phone_number, e.company_id, e.office_id" +
            ", COALESCE(c.name, '') as company_name, COALESCE(o.name, '') as office_name FROM employees e" +
            " LEFT OUTER JOIN companies c ON c.company_id = e.company_id AND NOT (c.state = ?)" +
            " LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = ?)";
    }

    public static String buildFindAll() {
        return
            baseSelectString() + " WHERE NOT (e.state = ?)";
    }

    public static String buildFindAllByCategoryId() {
        return
            baseSelectString() + " WHERE NOT (e.state = ?) AND e.category = ?";
    }
}
