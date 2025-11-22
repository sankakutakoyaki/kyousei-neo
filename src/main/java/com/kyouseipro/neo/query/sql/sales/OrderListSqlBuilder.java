package com.kyouseipro.neo.query.sql.sales;

public class OrderListSqlBuilder {
    
    private static String baseSelectString() {
        return
            "SELECT ord.order_id, ord.request_number, ord.order_date, ord.start_date, ord.end_date" +
            ", ord.title, ord.order_postal_code, ord.order_full_address" +
            ", prime_constractor_id, prime_constractor_office_id" +
            ", COALESCE(c.name, '') as prime_constractor_name, COALESCE(o.name, '') as prime_constractor_office_name, ord.version, ord.state FROM orders ord" +
            " LEFT OUTER JOIN companies c ON c.company_id = ord.prime_constractor_id AND NOT (c.state = ?)" +
            " LEFT OUTER JOIN offices o ON o.office_id = ord.prime_constractor_office_id AND NOT (o.state = ?)";
    }

    public static String buildFindAllSql() {
        return
            baseSelectString() + " WHERE NOT (ord.state = ?)";
    }

    public static String buildFindByBetweenOrderListEntity() {
        return 
            baseSelectString() +
            " WHERE NOT (ord.state = ?) AND " +
            "((ord.start_date >= ? AND ord.end_date <= ?) OR" +
            "(ord.start_date >= ? AND ord.end_date <= '9999-12-31') OR" +
            "(ord.start_date >= '9999-12-31' AND ord.end_date <= ?) OR" +
            "(ord.start_date >= '9999-12-31' AND ord.end_date <= '9999-12-31'))";
    }
}

