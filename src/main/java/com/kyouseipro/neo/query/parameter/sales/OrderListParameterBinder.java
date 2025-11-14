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
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.COMPLETE.getCode());
        ps.setInt(index++, Enums.state.COMPLETE.getCode());
        ps.setString(index++, start.toString());
        ps.setString(index++, end.toString());
    }
}
