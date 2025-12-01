package com.kyouseipro.neo.query.sql.corporation;

public class CompanyListSqlBuilder {
    
    public static String buildFindAllSql() {
        return "SELECT company_id, category, name, name_kana, tel_number, email FROM companies WHERE NOT (state = ?)";
    }

    public static String buildFindAllClientSql() {
        return "SELECT * FROM companies WHERE NOT (state = ?) AND NOT (category = ?)";
    }

    public static String buildFindAllComboOwnCompanySql() {
        return "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND (category = ?);";
    }

    public static String buildFindAllComboCompanySql() {
        return "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) ORDER BY name_kana, category;";
    }

    public static String buildFindAllComboClientSql() {
        return "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND NOT (category = ?) ORDER BY name_kana, category;";
    }

    public static String buildFindAllComboPrimeConstractorSql() {
        return "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND category = ? ORDER BY name_kana, category;";
    }

    public static String buildFindAllComboByCategorySql() {
        return "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND category = ?;";
    }
}
