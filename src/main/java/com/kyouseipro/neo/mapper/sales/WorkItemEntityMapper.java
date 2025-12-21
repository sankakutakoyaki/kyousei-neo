package com.kyouseipro.neo.mapper.sales;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.sales.WorkItemEntity;

public class WorkItemEntityMapper {
    public static WorkItemEntity map(ResultSet rs) throws SQLException {
        WorkItemEntity entity = new WorkItemEntity();
        entity.setWork_item_id(rs.getInt("work_item_id"));
        entity.setCode(rs.getInt("code"));
        entity.setCategory(rs.getInt("category"));
        entity.setName(rs.getString("name"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
