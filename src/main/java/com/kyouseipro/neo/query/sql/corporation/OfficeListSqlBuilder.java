package com.kyouseipro.neo.query.sql.corporation;

public class OfficeListSqlBuilder {
    
    private static String basicSelectString() {
        return
            "SELECT o.*, c.name as company_name, c.name_kana as company_name_kana FROM offices o" + 
            " INNER JOIN companies c ON c.company_id = o.company_id";
    }

    public static String buildFindAllSql() {
        return
            basicSelectString() +
            " WHERE NOT (state = ?)";
    }

    public static String buildFindAllByCategorySql() {
        return 
            basicSelectString() +
            " WHERE NOT (c.state = ?) AND NOT (o.state = ?) AND NOT (c.category = ?)";
    }

        public static String buildFindAllComboClientSql() {
        return "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND NOT (category = ?) ORDER BY name_kana;";
    }
}
