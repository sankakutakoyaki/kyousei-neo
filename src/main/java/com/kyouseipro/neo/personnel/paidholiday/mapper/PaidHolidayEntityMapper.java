package com.kyouseipro.neo.personnel.paidholiday.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.personnel.paidholiday.entity.PaidHolidayEntity;

public class PaidHolidayEntityMapper {
    public static PaidHolidayEntity map(ResultSet rs) throws SQLException {
        PaidHolidayEntity entity = new PaidHolidayEntity();
        entity.setPaid_holiday_id(rs.getInt("paid_holiday_id"));
        entity.setEmployee_id(rs.getInt("employee_id"));
        entity.setFull_name(rs.getString("full_name"));
        entity.setStart_date(Utilities.toLocalDate(rs, "start_date"));
        // entity.setStart_date(rs.getDate("start_date").toLocalDate());
        entity.setEnd_date(Utilities.toLocalDate(rs, "end_date"));
        // entity.setEnd_date(rs.getDate("end_date").toLocalDate());
        entity.setPermit_employee_id(rs.getInt("permit_employee_id"));
        entity.setReason(rs.getString("reason"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        
        return entity;
    }
}
