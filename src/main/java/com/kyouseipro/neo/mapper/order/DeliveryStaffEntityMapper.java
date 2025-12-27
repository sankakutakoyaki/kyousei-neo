package com.kyouseipro.neo.mapper.order;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.order.DeliveryStaffEntity;

public class DeliveryStaffEntityMapper {
    public static DeliveryStaffEntity map(ResultSet rs) throws SQLException {
        DeliveryStaffEntity entity = new DeliveryStaffEntity();
        entity.setDelivery_staff_id(rs.getInt("delivery_staff_id"));
        entity.setOrder_id(rs.getInt("order_id"));
        entity.setEmployee_id(rs.getInt("employee_id"));
        entity.setCompany_id(rs.getInt("company_id"));
        entity.setCompany_name(rs.getString("company_name"));
        entity.setOffice_id(rs.getInt("office_id"));
        entity.setOffice_name(rs.getString("office_name"));
        entity.setFull_name(rs.getString("full_name"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
