package com.kyouseipro.neo.mapper.sales;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.sales.OrderItemEntity;

public class OrderItemEntityMapper {    
    public static OrderItemEntity map(ResultSet rs) throws SQLException {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setOrder_item_id(rs.getInt("order_item_id"));
        entity.setOrder_id(rs.getInt("order_id"));
        entity.setItem_maker(rs.getString("item_maker"));
        entity.setItem_name(rs.getString("item_name"));
        entity.setItem_model(rs.getString("item_model"));
        entity.setItem_quantity(rs.getInt("item_quantity"));
        entity.setArrival_date(rs.getDate("arrival_date").toLocalDate());
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}