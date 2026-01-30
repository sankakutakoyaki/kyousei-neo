package com.kyouseipro.neo.sales.order.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.sales.order.entity.WorkContentEntity;

public class WorkContentEntityMapper {
    public static WorkContentEntity map(ResultSet rs) throws SQLException {
        WorkContentEntity entity = new WorkContentEntity();
        entity.setWorkContentId(rs.getInt("work_content_id"));
        entity.setOrderId(rs.getInt("order_id"));
        entity.setWorkContent(rs.getString("work_content"));
        entity.setWorkQuantity(rs.getInt("work_quantity"));
        entity.setWorkPayment(rs.getInt("work_payment"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}