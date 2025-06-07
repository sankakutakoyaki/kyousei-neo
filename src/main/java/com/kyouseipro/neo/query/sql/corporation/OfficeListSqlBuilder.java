package com.kyouseipro.neo.query.sql.corporation;

public class OfficeListSqlBuilder {
    
    private static String basicSelectString() {
        return
            "SELECT o.*, o.name as office_name, o.name_kana as office_name_kana FROM offices o" + 
            " INNER JOIN companies c ON c.company_id = o.company_id AND NOT (c.state = ?)";
    }

    public static String buildFindAllSql() {
        return
            basicSelectString() +
            " WHERE NOT (o.state = ?)";
    }

    public static String buildFindAllByCategorySql() {
        return 
            basicSelectString() +
            " WHERE NOT (o.state = ?) AND NOT (c.category = ?)";
    }

    public static String buildFindAllComboClientSql() {
        return
            "SELECT o.office_id as number, o.name as text FROM offices o" + 
            " LEFT OUTER JOIN companies c ON c.company_id = o.company_id AND NOT (c.state = ?)" +
            " WHERE NOT (o.state = ?) AND NOT (c.category = ?) ORDER BY o.name_kana;";
    }
}
