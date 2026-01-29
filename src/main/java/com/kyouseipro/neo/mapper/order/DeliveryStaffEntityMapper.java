package com.kyouseipro.neo.mapper.order;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.order.DeliveryStaffEntity;

public class DeliveryStaffEntityMapper {
    public static DeliveryStaffEntity map(ResultSet rs) throws SQLException {
        DeliveryStaffEntity entity = new DeliveryStaffEntity();
        entity.setDeliveryStaffId(rs.getInt("delivery_staff_id"));
        entity.setOrderId(rs.getInt("order_id"));
        entity.setEmployeeId(rs.getInt("employee_id"));
        entity.setCompanyId(rs.getInt("company_id"));
        entity.setCompanyName(rs.getString("company_name"));
        entity.setOfficeId(rs.getInt("office_id"));
        entity.setOfficeName(rs.getString("office_name"));
        entity.setFullName(rs.getString("full_name"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
