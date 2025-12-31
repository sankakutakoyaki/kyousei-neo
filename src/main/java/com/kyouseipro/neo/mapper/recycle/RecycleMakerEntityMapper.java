package com.kyouseipro.neo.mapper.recycle;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.recycle.RecycleMakerEntity;

public class RecycleMakerEntityMapper {
    public static RecycleMakerEntity map(ResultSet rs) throws SQLException {
        RecycleMakerEntity entity = new RecycleMakerEntity();
        entity.setRecycle_maker_id(rs.getInt("recycle_maker_id"));
        entity.setCode(rs.getInt("code"));
        entity.setName(rs.getString("name"));
        entity.setAbbr_name(rs.getString("abbr_name"));
        entity.setGroup(rs.getInt("group"));
        entity.setGroup_name(rs.getString("group_name"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
