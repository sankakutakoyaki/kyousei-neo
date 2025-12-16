package com.kyouseipro.neo.query.sql.sales;

public class KsSalesSqlBuilder {
    private static String baseSelectString() {
        return
            "SELECT k.* FROM ks_sales k" +
            " LEFT OUTER JOIN employees e ON REPLACE(REPLACE(e.full_name, ' ', ''), '　', '') = REPLACE(REPLACE(k.staff_name_1, ' ', ''), '　', '') AND NOT (e.state = ?)" +
            " LEFT OUTER JOIN companies c ON c.company_id = e.company_id AND NOT (c.state = ?)";
    }

    public static String buildFindByBetweenRecycleEntity(String col) {
        return 
            baseSelectString() +
            " WHERE k.delivery_date >= ? AND k.delivery_date < ?";
    }
}
