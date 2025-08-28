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
        entity.setWork_date(rs.getDate("work_date").toLocalDate());
        entity.setFull_name(rs.getString("full_name"));
        entity.setOffice_name(rs.getString("office_name"));
        LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();
        if (start != null) {
            entity.setStart_time(start.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString());
        }
        LocalDateTime end = rs.getTimestamp("end_time").toLocalDateTime();
        if (end != null) {
            entity.setEnd_time(end.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString());
        }
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
