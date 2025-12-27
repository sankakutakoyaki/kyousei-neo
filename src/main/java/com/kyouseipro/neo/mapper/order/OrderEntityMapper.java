package com.kyouseipro.neo.mapper.order;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.order.OrderEntity;

public class OrderEntityMapper {    
    public static OrderEntity map(ResultSet rs) throws SQLException {
        OrderEntity entity = new OrderEntity();
        entity.setOrder_id(rs.getInt("order_id"));
        entity.setRequest_number(rs.getString("request_number"));
        entity.setStart_date(rs.getDate("start_date").toLocalDate());
        entity.setEnd_date(rs.getDate("end_date").toLocalDate());
        entity.setPrime_constractor_id(rs.getInt("prime_constractor_id"));
        entity.setPrime_constractor_name(rs.getString("prime_constractor_name"));
        entity.setPrime_constractor_office_id(rs.getInt("prime_constractor_office_id"));
        entity.setPrime_constractor_office_name(rs.getString("prime_constractor_office_name"));
        entity.setTitle(rs.getString("title"));
        entity.setOrder_postal_code(rs.getString("order_postal_code"));
        entity.setOrder_full_address(rs.getString("order_full_address"));
        entity.setContact_information(rs.getString("contact_information"));
        entity.setContact_information2(rs.getString("contact_information2"));
        entity.setRemarks(rs.getString("remarks"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}