package com.kyouseipro.neo.query.parameter.order;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import com.kyouseipro.neo.common.Enums;

public class OrderListParameterBinder {
    public static int bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindByBetweenOrderListEntity(PreparedStatement ps, LocalDate start, LocalDate end) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, start.toString());
        ps.setString(index++, end.toString());
        ps.setString(index++, start.toString());
        ps.setString(index++, end.toString());
        return index;
    }
}
