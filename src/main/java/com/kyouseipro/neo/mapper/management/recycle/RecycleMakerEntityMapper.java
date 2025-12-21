package com.kyouseipro.neo.mapper.management.recycle;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.management.recycle.RecycleMakerEntity;

public class RecycleMakerEntityMapper {
    public static RecycleMakerEntity map(ResultSet rs) throws SQLException {
        RecycleMakerEntity entity = new RecycleMakerEntity();
        entity.setRecycle_maker_id(rs.getInt("recycle_maker_id"));
        entity.setCode(rs.getInt("code"));
        entity.setName(rs.getString("name"));
        entity.setGroup(rs.getString("group"));
        return entity;
    }
}
