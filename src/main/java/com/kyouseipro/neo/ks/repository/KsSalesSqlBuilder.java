package com.kyouseipro.neo.ks.repository;

public class KsSalesSqlBuilder {
    // private static String baseSelectString() {
    //     return
    //         "SELECT k.* FROM ks_sales k" +
    //         " LEFT OUTER JOIN employees e ON REPLACE(REPLACE(e.full_name, ' ', ''), '　', '') = REPLACE(REPLACE(k.staff_name_1, ' ', ''), '　', '') AND NOT (e.state = ?)" +
    //         " LEFT OUTER JOIN companies c ON c.company_id = e.company_id AND NOT (c.state = ?)";
    // }

    public static String buildFindByBetweenByStaff() {
        return 
            "SELECT k.store_name, c.name as staff_company, k.staff_name_1, sum(k.amount) as amount FROM ks_sales k" +
            " LEFT OUTER JOIN employees e ON REPLACE(REPLACE(e.full_name, ' ', ''), '　', '') = REPLACE(REPLACE(k.staff_name_1, ' ', ''), '　', '') AND NOT (e.state = ?)" +
            " LEFT OUTER JOIN companies c ON c.company_id = e.company_id AND NOT (c.state = ?)" +
            " WHERE k.delivery_date >= ? AND k.delivery_date < ?" +
            " GROUP BY k.store_name, c.name, k.staff_name_1;";
    }
}
