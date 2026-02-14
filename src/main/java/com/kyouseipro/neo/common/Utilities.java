package com.kyouseipro.neo.common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.interfaces.CodeEnum;
import com.kyouseipro.neo.interfaces.CsvExportable;

public class Utilities {
    /**
     * 第一引数に指定されたEnumの中から、第2引数のコード値と一致するものを取得する。
     *
     * @param target 取得したいEnumのクラス
     * @param code   検索するコード値
     * @param <E>    CodeInterfaceを実装したEnumクラス
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <E extends Enum & CodeEnum> E enumValueOf(Class<E> target, int num) {
        return Arrays.stream(target.getEnumConstants())
                .filter(data -> data.getCode() == num)
                .findFirst()
                .orElse(null);
    }

    /**
     * IN句用の数列を作成する
     * @param list
     * @return
     */
    public static List<Integer> createSequenceByIds(List<SimpleData> list) {
        List<Integer> ids = new ArrayList<>();
        list.forEach(value -> {
            ids.add(value.getNumber());
        });
        return ids;
    }

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
     * 
     * @param value
     * @return
     */
    public static String escapeCsv(Object value) {
        if (value == null) return "";

        String str = value.toString();

        if ("null".equals(str)) return "";

        if (str.contains(",") || str.contains("\n") || str.contains("\"")) {
            str = str.replace("\"", "\"\"");
            return "\"" + str + "\"";
        }

        return str;
    }

    /**
     * 
     * @param list
     * @param headers
     * @return
     */
    public static String toCsv(List<? extends CsvExportable> list, List<String> headers) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", headers)).append("\n");
        for (CsvExportable item : list) {
            sb.append(String.join(",", item.toCsvRow())).append("\n");
        }
        return sb.toString();
    }

    // 自社のcompanny_idを返す
    public static int getOwnCompanyId() {
        return 1000;
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
