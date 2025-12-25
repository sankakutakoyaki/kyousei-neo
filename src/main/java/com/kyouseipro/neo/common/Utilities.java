package com.kyouseipro.neo.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.interfaceis.CodeEnum;
import com.kyouseipro.neo.interfaceis.CsvExportable;

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
    // public static String createSequenceByIds(List<SimpleData> list) {
    //     StringBuilder idsStr = new StringBuilder();
    //     list.forEach(value -> {
    //         idsStr.append(value.getNumber()).append(", ");
    //     });
    //     return idsStr.substring(0, idsStr.length() - 2);
    // }
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
    public static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
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
}
