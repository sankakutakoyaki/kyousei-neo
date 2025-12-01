package com.kyouseipro.neo.mapper.sales;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.sales.OrderListEntity;

public class OrderListEntityMapper {    
    public static OrderListEntity map(ResultSet rs) throws SQLException {
        OrderListEntity entity = new OrderListEntity();
        entity.setOrder_id(rs.getInt("order_id"));
        entity.setRequest_number(rs.getString("request_number"));
        // entity.setOrder_date(rs.getDate("order_date").toLocalDate());
        entity.setStart_date(rs.getDate("start_date").toLocalDate());
        entity.setEnd_date(rs.getDate("end_date").toLocalDate());
        entity.setPrime_constractor_id(rs.getInt("prime_constractor_id"));
        entity.setPrime_constractor_name(rs.getString("prime_constractor_name"));
        entity.setPrime_constractor_office_id(rs.getInt("prime_constractor_office_id"));
        entity.setPrime_constractor_office_name(rs.getString("prime_constractor_office_name"));
        entity.setTitle(rs.getString("title"));
        entity.setOrder_postal_code(rs.getString("order_postal_code"));
        entity.setOrder_full_address(rs.getString("order_full_address"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}