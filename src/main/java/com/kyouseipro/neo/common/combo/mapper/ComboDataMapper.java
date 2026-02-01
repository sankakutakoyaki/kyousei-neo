package com.kyouseipro.neo.common.combo.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.common.combo.entity.ComboData;

public class ComboDataMapper {
    public static ComboData map(ResultSet rs) throws SQLException {
        ComboData entity = new ComboData();
        entity.setId(rs.getInt("id"));
        entity.setNumber(rs.getInt("number"));
        entity.setText(rs.getString("text"));
        return entity;
    }
}