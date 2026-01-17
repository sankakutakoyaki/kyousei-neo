package com.kyouseipro.neo.mapper.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.dto.SimpleData;

public class SimpleDataMapper {
    public static SimpleData map(ResultSet rs) throws SQLException {
        SimpleData entity = new SimpleData();
        entity.setNumber(rs.getInt("number"));
        entity.setText(rs.getString("text"));
        return entity;
    }
}
