package com.kyouseipro.neo.entity.regist;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class WorkPriceEntity implements CsvExportable  {
    private int work_price_id;
    private int work_item_id;
    private String work_item_name;
    private int company_id;
    private String company_name;
    private int price;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,作業名,荷主,金額";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(work_price_id)) + "," +
               Utilities.escapeCsv(work_item_name) + "," +
               Utilities.escapeCsv(company_name) + "," +
               Utilities.escapeCsv(String.valueOf(price));
    }
}
