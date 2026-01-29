package com.kyouseipro.neo.entity.work;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class WorkItemEntity implements CsvExportable {
    private int workItemId;
    private int fullCode;
    private int code;
    private int categoryId;
    private int categoryCode;
    private String categoryName;
    private String name;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "コード,分類,作業名";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(fullCode)) + "," +
               Utilities.escapeCsv(categoryName) + "," +
               Utilities.escapeCsv(name);
    }
}
