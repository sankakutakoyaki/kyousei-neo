package com.kyouseipro.neo.query.sql.corporation;

public class StaffListSqlBuilder {
    public static String buildFindAll() {
        return "SELECT s.*, c.name as company_name, o.name as office_name FROM staffs s" + 
            " INNER JOIN companies c ON c.company_id = s.company_id AND NOT (c.state = ?)" + 
            " LEFT OUTER JOIN offices o ON o.office_id = s.office_id AND NOT (o.state = ?)" + 
            " WHERE NOT (c.category = 0) AND NOT (s.state = ?)";
    }
}
