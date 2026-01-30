package com.kyouseipro.neo.recycle.item.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.recycle.item.entity.RecycleItemEntity;

public class RecycleItemEntityMapper {
    public static RecycleItemEntity map(ResultSet rs) throws SQLException {
        RecycleItemEntity entity = new RecycleItemEntity();
        entity.setRecycleItemId(rs.getInt("recycle_item_id"));
        entity.setCode(rs.getInt("code"));
        entity.setName(rs.getString("name"));
        return entity;
    }
}
