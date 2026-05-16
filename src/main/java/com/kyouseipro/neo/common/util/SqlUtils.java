package com.kyouseipro.neo.common.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SqlUtils {
    /**
     * プレースホルダー（？）の数を動的に変更する
     * @param count
     * @return
     */
    public static String generatePlaceholders(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> "?")
                .collect(Collectors.joining(", "));
    }

    /**
     * mapperでのnull回避
     * @param rs
     * @param column
     * @return
     * @throws SQLException
     */
    public static LocalDate toLocalDate(ResultSet rs, String column) throws SQLException {
        java.sql.Date date = rs.getDate(column);
        return date != null ? date.toLocalDate() : null;
    }

    /**
     * parameterでのnull処理
     * @param ps
     * @param index
     * @param value
     * @throws SQLException
     */
    public static void setLocalDate(PreparedStatement ps, int index, LocalDate value) throws SQLException {
        if (value != null) {
            ps.setDate(index, java.sql.Date.valueOf(value));
        } else {
            ps.setNull(index, java.sql.Types.DATE);
        }
    }
}
