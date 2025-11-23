package com.kyouseipro.neo.entity.sales;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class DeliveryStaffEntity implements CsvExportable{
    private int delivery_staff_id;
    private int order_id;
    private int employee_id;
    private int company_id;
    private String company_name;
    private int office_id;
    private String office_name;
    private String full_name;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,受注番号,会社名,支店名,担当者名";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(delivery_staff_id)) + "," +
               Utilities.escapeCsv(String.valueOf(order_id)) + "," +
               Utilities.escapeCsv(company_name) + "," +
               Utilities.escapeCsv(office_name) + "," +
               Utilities.escapeCsv(full_name) + ",";
    }
}
