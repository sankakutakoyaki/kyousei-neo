package com.kyouseipro.neo.query.parameter.company;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;

public class CompanyListParameterBinder {

    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
    }

    public static void bindFindAllClient(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, 0);
        ps.setInt(2, Enums.state.DELETE.getCode());
    }

    public static void bindFindAllByCategoryId(PreparedStatement ps, Integer categoryId) throws SQLException {
        ps.setInt(1, categoryId);
        ps.setInt(2, Enums.state.DELETE.getCode());
    }
}
