package com.kyouseipro.neo.entity.sales;

import java.time.LocalDate;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class KsSalesEntity implements CsvExportable {
    private int ks_sales_id;
    private String target_year_month;
    private int partner_store_code;
    private String partner_store_name;
    private int corporation_code;
    private String corporation_name;
    private int store_code;
    private String store_name;
    private LocalDate slip_date;
    private int purchase_slip_number;
    private String purchase_slip_type;
    private String jan_code;
    private String model_number;
    private int quantity;
    private int amount;
    private int delivery_payment_mgmt_number;
    private int sales_slip_number;
    private int sales_store_code;
    private String sales_store_name;
    private String staff_company;
    private int staff_code_1;
    private String staff_name_1;
    private int staff_code_2;
    private String staff_name_2;
    private LocalDate delivery_date;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,対象年月,協力店コード,協力店名,法人コード,法人名,店舗ｺｰﾄﾞ,店舗名,伝票日付,仕入伝票番号,仕入伝票種別,JANコード,型番,数量,金額,配送入出金管理番号,売上伝票番号,売上店舗コード,売上店舗名,担当者コード1,担当者名1,担当者コード2,担当者名2,配送日";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(ks_sales_id)) + "," +
               Utilities.escapeCsv(target_year_month) + ","  +
               Utilities.escapeCsv(String.valueOf(partner_store_code)) + "," +
               Utilities.escapeCsv(partner_store_name) + "," +
               Utilities.escapeCsv(String.valueOf(corporation_code)) + "," +
               Utilities.escapeCsv(corporation_name) + "," +
               Utilities.escapeCsv(String.valueOf(store_code)) + "," +
               Utilities.escapeCsv(store_name) + "," +
               Utilities.escapeCsv(String.valueOf(slip_date)) + "," +
               Utilities.escapeCsv(String.valueOf(purchase_slip_number)) + "," +
               Utilities.escapeCsv(purchase_slip_type) + "," +
               Utilities.escapeCsv(jan_code) + "," +
               Utilities.escapeCsv(model_number) + "," +
               Utilities.escapeCsv(String.valueOf(quantity)) + "," +
               Utilities.escapeCsv(String.valueOf(amount)) + "," +
               Utilities.escapeCsv(String.valueOf(delivery_payment_mgmt_number)) + "," +
               Utilities.escapeCsv(String.valueOf(sales_slip_number)) + "," +
               Utilities.escapeCsv(String.valueOf(sales_store_code)) + "," +
               Utilities.escapeCsv(sales_store_name) + "," +
               Utilities.escapeCsv(String.valueOf(staff_code_1)) + "," +
               Utilities.escapeCsv(staff_name_1) + "," +
               Utilities.escapeCsv(String.valueOf(staff_code_2)) + "," +
               Utilities.escapeCsv(staff_name_2) + "," +
               Utilities.escapeCsv(String.valueOf(delivery_date));
    }
}
