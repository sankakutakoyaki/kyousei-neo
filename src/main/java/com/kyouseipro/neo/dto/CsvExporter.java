package com.kyouseipro.neo.dto;

import java.util.List;

import com.kyouseipro.neo.interfaces.CsvExportable;

public class CsvExporter {
    public static <T extends CsvExportable> String export(List<T> items, Class<T> clazz) {
        StringBuilder sb = new StringBuilder();

        // 反射でクラスの static メソッド getCsvHeader() を呼ぶ
        try {
            String header = (String) clazz.getMethod("getCsvHeader").invoke(null);
            sb.append(header).append("\n");
        } catch (Exception e) {
            // もし getCsvHeader がなければ空行にしておく
            sb.append("\n");
        }

        for (CsvExportable item : items) {
            sb.append(item.toCsvRow()).append("\n");
        }
        return sb.toString();
    }
}
