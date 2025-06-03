package com.kyouseipro.neo.query.sql.company;

public class CompanyListSqlBuilder {
    
    public static String buildFindAllSql() {
        return "SELECT * FROM companies WHERE NOT (state = ?)";
    }

    public static String buildFindAllClientSql() {
        return "SELECT * FROM companies WHERE NOT (category = ?) AND NOT (state = ?)";
    }

    public static String buildFindAllClientSql(int categoryId) {
        return "SELECT * FROM companies WHERE NOT (category = ?) AND NOT (state = ?)";
    }
}
