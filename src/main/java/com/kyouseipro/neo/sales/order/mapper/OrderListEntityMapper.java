package com.kyouseipro.neo.sales.order.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.sales.order.entity.OrderListEntity;

public class OrderListEntityMapper {    
    public static OrderListEntity map(ResultSet rs) throws SQLException {
        OrderListEntity entity = new OrderListEntity();
        entity.setOrderId(rs.getInt("order_id"));
        entity.setRequestNumber(rs.getString("request_number"));
        // entity.setOrder_date(rs.getDate("order_date").toLocalDate());
        entity.setStartDate(rs.getDate("start_date").toLocalDate());
        entity.setEndDate(rs.getDate("end_date").toLocalDate());
        entity.setPrimeConstractorId(rs.getInt("prime_constractor_id"));
        entity.setPrimeConstractorName(rs.getString("prime_constractor_name"));
        entity.setPrimeConstractorOfficeId(rs.getInt("prime_constractor_office_id"));
        entity.setPrimeConstractorOfficeName(rs.getString("prime_constractor_office_name"));
        entity.setTitle(rs.getString("title"));
        entity.setOrderPostalCode(rs.getString("order_postal_code"));
        entity.setOrderFullAddress(rs.getString("order_full_address"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}