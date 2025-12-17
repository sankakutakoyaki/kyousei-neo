package com.kyouseipro.neo.query.parameter.sales;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kyouseipro.neo.common.Enums;

public class KsSalesParameterBinder {
    public static void bindFindByBetween(PreparedStatement ps, LocalDate start, LocalDate end) throws SQLException {
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to   = end.plusDays(1).atStartOfDay();
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());

        ps.setTimestamp(index++, Timestamp.valueOf(from));
        ps.setTimestamp(index++, Timestamp.valueOf(to));
    }
}
