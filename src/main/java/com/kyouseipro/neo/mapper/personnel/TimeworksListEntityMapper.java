package com.kyouseipro.neo.mapper.personnel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;

public class TimeworksListEntityMapper {
    public static TimeworksListEntity map(ResultSet rs) throws SQLException {
        TimeworksListEntity entity = new TimeworksListEntity();
        entity.setTimeworks_id(rs.getInt("timeworks_id"));
        entity.setEmployee_id(rs.getInt("employee_id"));
        entity.setCategory(rs.getInt("category"));
        entity.setFull_name(rs.getString("full_name"));
        entity.setOffice_name(rs.getString("office_name"));
        if (rs.getTimestamp("start_time") != null){
            LocalDateTime startTime = rs.getTimestamp("comp_start_time").toLocalDateTime();
            entity.setStart_time(startTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString());
        }
        if (rs.getTimestamp("end_time") != null) {
            LocalDateTime endTime = rs.getTimestamp("comp_end_time").toLocalDateTime();
            entity.setEnd_time(endTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString());
        }
        return entity;
    }
}
