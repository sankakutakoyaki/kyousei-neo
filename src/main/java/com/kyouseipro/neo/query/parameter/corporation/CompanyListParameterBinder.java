package com.kyouseipro.neo.query.parameter.corporation;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;

public class CompanyListParameterBinder {

    public static int bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindAllClient(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, 0);
        return index;
    }

    public static int bindFindAllByCategoryId(PreparedStatement ps, int categoryId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, categoryId);
        return index;
    }

    public static int bindFindAllComboOwnCompany(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, 0);
        return index;
    }

    public static int bindFindAllComboCompany(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindAllComboClient(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, 0);
        return index;
    }

    public static int bindFindAllComboPrimeConstractor(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.clientCategory.SHIPPER.getCode());
        return index;
    }

    public static int bindFindAllComboByCategory(PreparedStatement ps, int category) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, category);
        return index;
    }
}
