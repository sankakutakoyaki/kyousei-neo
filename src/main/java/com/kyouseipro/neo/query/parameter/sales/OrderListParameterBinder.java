package com.kyouseipro.neo.query.parameter.sales;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import com.kyouseipro.neo.common.Enums;

public class OrderListParameterBinder {
    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
    }

    public static void bindFindByBetweenOrderListEntity(PreparedStatement ps, LocalDate start, LocalDate end) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.COMPLETE.getCode());
        ps.setString(3, start.toString());
        ps.setString(4, end.toString());
    }
}
