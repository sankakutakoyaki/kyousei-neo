package com.kyouseipro.neo.query.sql.corporation;

public class CompanyListSqlBuilder {
    
    public static String buildFindAll() {
        return "SELECT company_id, category, name, name_kana, tel_number, email FROM companies WHERE NOT (state = ?)";
    }

    public static String buildFindAllClient() {
        return "SELECT * FROM companies WHERE NOT (state = ?) AND NOT (category = ?)";
    }

    public static String buildFindAllComboOwnCompany() {
        return "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND (category = ?);";
    }

    public static String buildFindAllCombo() {
        return "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) ORDER BY name_kana, category;";
    }

    public static String buildFindAllClientCombo() {
        return "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND NOT (category = ?) ORDER BY name_kana, category;";
    }

    public static String buildFindAllPrimeConstractorCombo() {
        return "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND category = ? ORDER BY name_kana, category;";
    }

    public static String buildFindAllPrimeConstractorComboHasOriginalPrice() {
        return "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND category = ? AND is_original_price = ? ORDER BY name_kana, category;";
    }

    public static String buildFindAllComboByCategory() {
        return "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND category = ?;";
    }
}
