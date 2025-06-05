package com.kyouseipro.neo.query.parameter.corporation;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;

public class OfficeListParameterBinder {
    
    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
    }

    // public static void bindFindAllClient(PreparedStatement ps, Void unused) throws SQLException {
    //     ps.setInt(1, 0); // 1番目の ?
    //     ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
    //     ps.setInt(3, Enums.state.DELETE.getCode());     // 3番目の ?
    // }

     public static void bindFindAllByCategoryId(PreparedStatement ps, Integer categoryId) throws SQLException {
        ps.setInt(1, categoryId);
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
    }
}
