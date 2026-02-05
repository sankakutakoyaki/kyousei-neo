package com.kyouseipro.neo.dto.sql.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class SqlUtil {
    public static void setDateOrMax(
            PreparedStatement ps,
            int index,
            LocalDate date
    ) throws SQLException {
        LocalDate v = (date != null) ? date : LocalDate.of(9999, 12, 31);
        ps.setDate(index, java.sql.Date.valueOf(v));
    }
}
