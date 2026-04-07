package com.kyouseipro.neo.sql.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kyouseipro.neo.sql.model.CsvColumn;

public class CsvBuilder {

    public static String build(List<Map<String, Object>> list, List<CsvColumn> columns) {

        StringBuilder sb = new StringBuilder();

        // ヘッダー
        List<String> headers = columns.stream()
            .map(CsvColumn::getLabel)
            .toList();

        sb.append(String.join(",", headers)).append("\n");

        // データ
        for (Map<String, Object> row : list) {

            List<String> values = new ArrayList<>();

            for (CsvColumn col : columns) {
                Object val = row.get(col.getKey());
                values.add(escape(val));
            }

            sb.append(String.join(",", values)).append("\n");
        }

        return sb.toString();
    }

    private static String escape(Object val) {
        if (val == null) return "";

        String str = val.toString();
        str = str.replace("\"", "\"\"");

        if (str.contains(",") || str.contains("\n") || str.contains("\"")) {
            str = "\"" + str + "\"";
        }

        return str;
    }
}
