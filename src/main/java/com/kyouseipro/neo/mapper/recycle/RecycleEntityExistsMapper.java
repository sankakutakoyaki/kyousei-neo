package com.kyouseipro.neo.mapper.recycle;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.recycle.RecycleEntity;

public class RecycleEntityExistsMapper {
    public static RecycleEntity map(ResultSet rs) throws SQLException {
        RecycleEntity entity = new RecycleEntity();
        entity.setRecycle_id(rs.getInt("recycle_id"));
        entity.setNumber(rs.getString("number"));
        return entity;
    }
}