package com.kyouseipro.neo.query.parameter.personnel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import com.kyouseipro.neo.common.Enums;

public class TimeworksListParameterBinder {

    public static void bindFindById(PreparedStatement ps, Integer employeeId) throws SQLException {
        ps.setInt(1, employeeId);
        ps.setInt(2, Enums.state.DELETE.getCode());
    }

    public static void bindFindByDate(PreparedStatement ps, LocalDate date) throws SQLException {
        ps.setString(1, date.toString());
        ps.setInt(2, Enums.state.DELETE.getCode());
    }
}
