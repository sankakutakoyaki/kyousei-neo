package com.kyouseipro.neo.query.sql.corporation;

public class OfficeListSqlBuilder {
    
    private static String basicSelectString() {
        return
            "SELECT o.*, c.name as company_name, c.name_kana as company_name_kana FROM offices o" + 
            " INNER JOIN companies c ON c.company_id = o.company_id AND NOT (c.state = ?)";
    }

    public static String buildFindAll() {
        return
            basicSelectString() +
            " WHERE NOT (o.state = ?);";
    }

    public static String buildFindAllOrderByKana() {
        return
            basicSelectString() +
            " WHERE NOT (o.state = ?) ORDER BY name_kana;";
    }

    public static String buildFindAllCombo() {
        return "SELECT office_id as number, name as text FROM offices WHERE NOT (state = ?) ORDER BY name_kana;";
    }

    public static String buildFindAllByCategory() {
        return 
            basicSelectString() +
            " WHERE NOT (o.state = ?) AND NOT (c.category = ?)";
    }

    public static String buildFindAllClientCombo() {
        return
            "SELECT o.office_id as number, o.name as text FROM offices o" + 
            " INNER JOIN companies c ON c.company_id = o.company_id AND NOT (c.state = ?)" +
            " WHERE NOT (o.state = ?) AND NOT (c.category = ?) ORDER BY o.name_kana;";
    }
}
