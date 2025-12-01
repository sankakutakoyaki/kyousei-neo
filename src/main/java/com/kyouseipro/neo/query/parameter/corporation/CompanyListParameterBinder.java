package com.kyouseipro.neo.query.parameter.corporation;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;

public class CompanyListParameterBinder {

    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
    }

    public static void bindFindAllClient(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, 0);
    }

    public static void bindFindAllByCategoryId(PreparedStatement ps, int categoryId) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, categoryId);
    }

    public static void bindFindAllComboOwnCompany(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, 0);
    }

    public static void bindFindAllComboCompany(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
    }

    public static void bindFindAllComboClient(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, 0);
    }

    public static void bindFindAllComboPrimeConstractor(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.clientCategory.SHIPPER.getCode());
    }

    public static void bindFindAllComboByCategory(PreparedStatement ps, int category) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, category);
    }
}
