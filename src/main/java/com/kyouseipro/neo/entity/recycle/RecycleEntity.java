package com.kyouseipro.neo.entity.recycle;

import java.time.LocalDate;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class RecycleEntity implements CsvExportable {
    private int recycle_id;
    private String number;//　お問合せ管理票番号
    private int maker_id;//　メーカー
    private String maker_name;
    private int item_id;//　品目・料金区分
    private String item_name;
    private LocalDate use_date;//　使用日
    private LocalDate delivery_date;//　引渡日
    private LocalDate shipping_date;//　発送日
    private LocalDate loss_date;//　ロス処理日
    private int company_id;//　小売業者
    private String company_name;
    private int office_id;
    private String office_name;
    private int recycle_fee;//　法定リサイクル料
    private int disposal_site_id;//　処分場
    private String disposal_site_name;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(recycle_id)) + "," +
               Utilities.escapeCsv(number) + "," +
               Utilities.escapeCsv(String.valueOf(maker_id)) + "," +
               Utilities.escapeCsv(maker_name) + "," +
               Utilities.escapeCsv(String.valueOf(item_id)) + "," +
               Utilities.escapeCsv(item_name) + "," + 
               Utilities.escapeCsv(String.valueOf(use_date)) + "," +
               Utilities.escapeCsv(String.valueOf(delivery_date)) + "," +
               Utilities.escapeCsv(String.valueOf(shipping_date)) + "," +
               Utilities.escapeCsv(String.valueOf(loss_date)) + "," +
               Utilities.escapeCsv(company_name) + "," +
               Utilities.escapeCsv(office_name) + "," +
               Utilities.escapeCsv(String.valueOf(recycle_fee)) + "," +
               Utilities.escapeCsv(String.valueOf(disposal_site_name));
    }
}
