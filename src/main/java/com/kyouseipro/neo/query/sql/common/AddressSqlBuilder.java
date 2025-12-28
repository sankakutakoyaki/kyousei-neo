package com.kyouseipro.neo.query.sql.common;

public class AddressSqlBuilder {

    public static String buildFindByPostalCode() {
        return "SELECT * FROM address WHERE NOT (state = ?) AND postal_code = ?";
    }
}
