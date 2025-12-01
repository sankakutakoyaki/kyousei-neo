package com.kyouseipro.neo.query.parameter.corporation;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;

public class StaffListParameterBinder {
    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
    }

    public static void bindFindBySalesStaff(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.clientCategory.SHIPPER.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
    }
}
