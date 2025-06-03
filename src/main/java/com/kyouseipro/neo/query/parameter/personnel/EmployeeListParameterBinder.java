package com.kyouseipro.neo.query.parameter.personnel;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;

public class EmployeeListParameterBinder {
    
    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
    }

    public static void bindFindAllByCategoryId(PreparedStatement ps, Integer categoryId) throws SQLException {
        ps.setInt(1, categoryId);
        ps.setInt(2, Enums.state.DELETE.getCode());
    }
}
