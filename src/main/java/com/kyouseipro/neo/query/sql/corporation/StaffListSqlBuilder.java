package com.kyouseipro.neo.query.sql.corporation;

public class StaffListSqlBuilder {
    public static String buildFindAllSql() {
        return "SELECT s.staff_id, s.name, s.name_kana, c.name as company_name, o.name as office_name FROM staffs s" + 
            " INNER LEFT OUTER JOIN companies c ON c.company_id = s.company_id" + 
            " INNER LEFT OUTER JOIN offices o ON o.office_id = s.office_id" + 
            " WHERE NOT (c.category = 0) AND NOT (s.state = ?) AND NOT (c.state = ?) AND NOT (o.state = ?)";
    }
}
