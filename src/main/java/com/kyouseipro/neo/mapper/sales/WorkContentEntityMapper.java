package com.kyouseipro.neo.mapper.sales;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.sales.WorkContentEntity;

public class WorkContentEntityMapper {
    public static WorkContentEntity map(ResultSet rs) throws SQLException {
        WorkContentEntity entity = new WorkContentEntity();
        entity.setWork_content_id(rs.getInt("work_content_id"));
        entity.setOrder_id(rs.getInt("order_id"));
        entity.setWork_content(rs.getString("work_content"));
        entity.setWork_quantity(rs.getInt("work_quantity"));
        entity.setWork_payment(rs.getInt("work_payment"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}