package com.kyouseipro.neo.mapper.work;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.work.WorkItemEntity;

public class WorkItemEntityMapper {
    public static WorkItemEntity map(ResultSet rs) throws SQLException {
        WorkItemEntity entity = new WorkItemEntity();
        entity.setWorkItemId(rs.getInt("work_item_id"));
        entity.setFullCode(rs.getInt("full_code"));
        entity.setCode(rs.getInt("code"));
        entity.setCategoryId(rs.getInt("category_id"));
        entity.setCategoryCode(rs.getInt("category_code"));
        entity.setCategoryName(rs.getString("category_name"));
        entity.setName(rs.getString("name"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
