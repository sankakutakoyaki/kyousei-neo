package com.kyouseipro.neo.mapper.personnel;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.personnel.TimeworksSummaryEntity;

public class TimeworksSummaryEntityMapper {
    public static TimeworksSummaryEntity map(ResultSet rs) throws SQLException {
        TimeworksSummaryEntity entity = new TimeworksSummaryEntity();
        entity.setEmployee_id(rs.getInt("employee_id"));
        entity.setFull_name(rs.getString("full_name"));
        entity.setTotal_working_date(rs.getInt("total_working_date"));
        entity.setTotal_working_time(rs.getDouble("total_working_time"));
        return entity;
    }
}
