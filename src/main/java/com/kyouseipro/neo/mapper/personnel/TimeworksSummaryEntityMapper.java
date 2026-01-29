package com.kyouseipro.neo.mapper.personnel;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.personnel.TimeworksSummaryEntity;

public class TimeworksSummaryEntityMapper {
    public static TimeworksSummaryEntity map(ResultSet rs) throws SQLException {
        TimeworksSummaryEntity entity = new TimeworksSummaryEntity();
        entity.setEmployeeId(rs.getInt("employee_id"));
        entity.setFullName(rs.getString("full_name"));
        entity.setTotalWorkingDate(rs.getInt("total_working_date"));
        entity.setTotalWorkingTime(rs.getDouble("total_working_time"));
        return entity;
    }
}
