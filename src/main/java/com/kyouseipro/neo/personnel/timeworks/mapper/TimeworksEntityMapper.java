package com.kyouseipro.neo.personnel.timeworks.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.personnel.timeworks.entity.TimeworksEntity;

public class TimeworksEntityMapper {
    public static TimeworksEntity map(ResultSet rs) throws SQLException {
        // try {
            TimeworksEntity e = new TimeworksEntity();

            e.setTimeworksId(rs.getInt("timeworks_id"));
            e.setEmployeeId(rs.getInt("employee_id"));
            e.setFullName(rs.getString("full_name"));
            e.setOfficeName(rs.getString("office_name"));

            Timestamp startTs = rs.getTimestamp("start_dt");
            if (startTs != null) {
                e.setStartDt(startTs.toLocalDateTime());
            }

            Timestamp endTs = rs.getTimestamp("end_dt");
            if (endTs != null) {
                e.setEndDt(endTs.toLocalDateTime());
            }

            e.setBreakMinutes(rs.getInt("break_minutes"));
            e.setWorkBaseDate(rs.getDate("work_base_date").toLocalDate());

            e.setStartLatitude(rs.getBigDecimal("start_latitude"));
            e.setStartLongitude(rs.getBigDecimal("start_longitude"));
            e.setEndLatitude(rs.getBigDecimal("end_latitude"));
            e.setEndLongitude(rs.getBigDecimal("end_longitude"));

            /** ★ enum 変換 */
            e.setWorkType(Enums.timeworksType.from(rs.getInt("work_type")));
            e.setState(Enums.timeworksState.from(rs.getInt("state")));

            return e;
        // } catch (SQLException ex) {
        //     throw new RuntimeException("ResultSet mapping error", ex);
        // }
    }
}