package com.kyouseipro.neo.sql.common;

import java.util.List;

import com.kyouseipro.neo.interfaces.CsvExportable;

public class CsvUtils {
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
}
