package com.kyouseipro.neo.query.parameter.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;

public class AddressParameterBinder {

    public static int bindFindByPostalCode(PreparedStatement ps, String postalCode) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, postalCode);
        return index;
    }
}
