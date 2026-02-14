package com.kyouseipro.neo.work.price.entity;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaces.CsvExportable;

import lombok.Data;

@Data
public class WorkPriceEntity implements CsvExportable  {
    private int workPriceId;
    private int workItemId;
    private String workItemName;
    private int fullCode;
    private int companyId;
    private String companyName;
    private int categoryId;
    private String categoryName;
    private int price;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,作業名,荷主,金額";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(workPriceId) + "," +
               Utilities.escapeCsv(workItemName) + "," +
               Utilities.escapeCsv(companyName) + "," +
               Utilities.escapeCsv(price);
    }
}
