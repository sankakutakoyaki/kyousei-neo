package com.kyouseipro.neo.interfaceis;

public interface CsvExportable {
    /**
     * CSVのヘッダー行（カンマ区切り）
     */
    static String getCsvHeader() {
        return "";
    }

    /**
     * CSVの1行分（カンマ区切り）
     */
    String toCsvRow();
}

