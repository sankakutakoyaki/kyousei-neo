package com.kyouseipro.neo.query.sql.sales;

public class OrderListSqlBuilder {
    
    private static String baseSelectString() {
        return
            "SELECT or.order_id, or.request_number, or.order_date, or.start_date, or.end_date, or.title, or.order_postal_code, or.order_full_address" +
            ", COALESCE(c.name, '') as prime_constractor_name, COALESCE(o.name, '') as prime_constractor_office_name FROM orders or" +
            " LEFT OUTER JOIN companies c ON c.company_id = or.prime_constrator_id AND NOT (c.state = ?)" +
            " LEFT OUTER JOIN offices o ON o.office_id = or.prime_constractor_office_id AND NOT (o.state = ?)";
    }

    public static String buildFindAllSql() {
        return
            baseSelectString() + " WHERE NOT (or.state = ?)";
    }

    public static String buildFindByBetweenOrderListEntity() {
        return 
        baseSelectString() +
        " WHERE NOT (or.state = ?) AND or.start_date >= ? AND or.end_date <= ?";
    }
}

