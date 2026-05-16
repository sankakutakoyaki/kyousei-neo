package com.kyouseipro.neo._backup.simpledata.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo._backup.simpledata.entity.SimpleData;

public class SimpleDataMapper {
    public static SimpleData map(ResultSet rs) throws SQLException {
        SimpleData entity = new SimpleData();
        entity.setNumber(rs.getInt("number"));
        entity.setText(rs.getString("text"));
        return entity;
    }
}
