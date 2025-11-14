package com.kyouseipro.neo.mapper.sales;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.sales.OrderEntity;

public class OrderEntityMapper {    
    public static OrderEntity map(ResultSet rs) throws SQLException {
        OrderEntity entity = new OrderEntity();
        entity.setOrder_id(rs.getInt("order_id"));
        entity.setRequest_number(rs.getString("request_number"));
        entity.setOrder_date(rs.getDate("order_date").toLocalDate());
        entity.setStart_date(rs.getDate("start_date").toLocalDate());
        entity.setEnd_date(rs.getDate("end_date").toLocalDate());
        entity.setPrime_contractor_id(rs.getInt("prime_contractor_id"));
        entity.setPrime_contractor_office_id(rs.getInt("prime_contractor_office_id"));
        entity.setTitle(rs.getString("title"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}