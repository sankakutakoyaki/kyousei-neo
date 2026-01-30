package com.kyouseipro.neo.sales.order.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.sales.order.entity.OrderEntity;

public class OrderEntityMapper {    
    public static OrderEntity map(ResultSet rs) throws SQLException {
        OrderEntity entity = new OrderEntity();
        entity.setOrderId(rs.getInt("order_id"));
        entity.setRequestNumber(rs.getString("request_number"));
        entity.setStartDate(rs.getDate("start_date").toLocalDate());
        entity.setEndDate(rs.getDate("end_date").toLocalDate());
        entity.setPrimeConstractorId(rs.getInt("prime_constractor_id"));
        entity.setPrimeConstractorName(rs.getString("prime_constractor_name"));
        entity.setPrimeConstractorOfficeId(rs.getInt("prime_constractor_office_id"));
        entity.setPrimeConstractorOfficeName(rs.getString("prime_constractor_office_name"));
        entity.setTitle(rs.getString("title"));
        entity.setOrderPostalCode(rs.getString("order_postal_code"));
        entity.setOrderFullAddress(rs.getString("order_full_address"));
        entity.setContactInformation(rs.getString("contact_information"));
        entity.setContactInformation2(rs.getString("contact_information2"));
        entity.setRemarks(rs.getString("remarks"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}