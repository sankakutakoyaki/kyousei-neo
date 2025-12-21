package com.kyouseipro.neo.mapper.management.recycle;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.management.recycle.RecycleItemEntity;

public class RecycleItemEntityMapper {
    public static RecycleItemEntity map(ResultSet rs) throws SQLException {
        RecycleItemEntity entity = new RecycleItemEntity();
        entity.setRecycle_item_id(rs.getInt("recycle_item_id"));
        entity.setCode(rs.getInt("code"));
        entity.setName(rs.getString("name"));
        return entity;
    }
}
