package com.kyouseipro.neo.personnel.paidholiday.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.personnel.paidholiday.entity.PaidHolidayListEntity;

public class PaidHolidayListEntityMapper {
    public static PaidHolidayListEntity map(ResultSet rs) throws SQLException {
        PaidHolidayListEntity entity = new PaidHolidayListEntity();
        entity.setEmployee_id(rs.getInt("employee_id"));
        entity.setFull_name(rs.getString("full_name"));
        entity.setTotal_days(rs.getInt("total_days"));
        
        return entity;
    }
}
