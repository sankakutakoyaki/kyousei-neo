package com.kyouseipro.neo.entity.regist;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class WorkItemEntity implements CsvExportable {
    private int work_item_id;
    private int full_code;
    private int code;
    private int category_id;
    private int category_code;
    private String category_name;
    private String name;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "コード,分類,作業名";
    }

    @Override
    public String toCsvRow() {
        // return Utilities.escapeCsv(String.format("%02d", category_code) + String.format("%02d", code)) + "," +
        return Utilities.escapeCsv(String.valueOf(full_code)) + "," +
               Utilities.escapeCsv(category_name) + "," +
               Utilities.escapeCsv(name);
    }
}
