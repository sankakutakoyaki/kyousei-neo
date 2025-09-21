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
        // 打刻データ
        LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();
        if (start != null) {
            entity.setStart_time(start.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString());
        }
        LocalDateTime end = rs.getTimestamp("end_time").toLocalDateTime();
        if (end != null) {
            entity.setEnd_time(end.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString());
        }

        // 基準データ
        LocalDateTime basicStart = rs.getTimestamp("basic_start_time").toLocalDateTime();
        String basicStartStr = "00:00:00";
        if (basicStart != null) {
            basicStartStr = basicStart.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString();
        }
        entity.setBasic_start_time(basicStartStr);
        LocalDateTime basicEnd = rs.getTimestamp("basic_end_time").toLocalDateTime();
        String basicEndStr = "00:00:00";
        if (basicEnd != null) {
            basicEndStr = basicEnd.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString();
        }
        entity.setBasic_end_time(basicEndStr);

        // 確定データ
        LocalDateTime compStart = rs.getTimestamp("comp_start_time").toLocalDateTime();
        if (compStart != null) {
            // if (compStart.toString().isEmpty() || compStart.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            //     entity.setComp_start_time(basicStartStr);
            if (compStart.toString().isEmpty()) {
                entity.setComp_start_time("00:00:00");
            } else {
                entity.setComp_start_time(compStart.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString());
            }
        }
        LocalDateTime compEnd = rs.getTimestamp("comp_end_time").toLocalDateTime();
        if (compEnd != null) {
            // if (compEnd.toString().isEmpty() || compEnd.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            //     entity.setComp_end_time(basicEndStr);
            if (compEnd.toString().isEmpty()) {
                entity.setComp_end_time("00:00:00");
            } else {
                entity.setComp_end_time(compEnd.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString());
            }
        }

        // 休憩
        LocalDateTime rest = rs.getTimestamp("rest_time").toLocalDateTime();
        if (rest != null) {
            // entity.setRest_time(rest.format(DateTimeFormatter.ofPattern("HH:mm")).toString());
            entity.setRest_time(rest.toLocalTime().toString());
        } else {
            entity.setRest_time("0");
        }

        //　状態
        entity.setSituation(rs.getString("situation"));
        // 有給
        // entity.setPaid_holiday_id(rs.getInt("paid_holiday_id"));
        // int paidHolidayId = rs.getInt("paid_holiday_id");
        // if (paidHolidayId > 0) {
        //     entity.setSituation("有給");
        // }
        
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
