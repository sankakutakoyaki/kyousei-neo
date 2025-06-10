package com.kyouseipro.neo.query.parameter.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;

public class AddressParameterBinder {

    public static void bindFindByPostalCode(PreparedStatement ps, String postalCode) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setString(2, postalCode);
    }
}
