package com.kyouseipro.neo.sql.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SqlUtil {
    public static void setDateOrMax(
            PreparedStatement ps,
            int index,
            LocalDate date
    ) throws SQLException {
        LocalDate v = (date != null) ? date : LocalDate.of(9999, 12, 31);
        ps.setDate(index, java.sql.Date.valueOf(v));
    }

    public static String toSnake(String camel) {
        return camel
                .replaceAll("([a-z0-9])([A-Z])", "$1_$2")
                .toLowerCase();
    }
    
    public static String toCamel(String snake) {
        StringBuilder result = new StringBuilder();
        boolean upper = false;

        for (char c : snake.toCharArray()) {
            if (c == '_') {
                upper = true;
            } else {
                result.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c));
                upper = false;
            }
        }

        return result.toString();
    }

    public static String placeholders(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> "?")
                .collect(Collectors.joining(","));
    }
}