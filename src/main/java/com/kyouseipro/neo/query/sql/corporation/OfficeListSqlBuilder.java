package com.kyouseipro.neo.query.sql.corporation;

public class OfficeListSqlBuilder {
    
    public static String buildFindAllSql() {
        return "SELECT * FROM offices WHERE NOT (state = ?)";
    }

    public static String buildFindAllByCategorySql() {
        return "SELECT o.*, c.name as company_name, c.name_kana as company_name_kana FROM offices o" + 
            " INNER JOIN companies c ON c.company_id = o.company_id" + 
            " WHERE NOT (c.category = ?) AND NOT (c.state = ?) AND NOT (o.state = ?)";
    }
}
