package com.kyouseipro.neo.entity.sales;

import java.time.LocalDate;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class OrderEntity implements CsvExportable {
    private int order_id;
    private String request_number;
    private LocalDate order_date;
    private LocalDate start_date;
    private LocalDate end_date;
    private int prime_contractor_id;
    private int prime_contractor_office_id;
    private String title;

    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,依頼番号,受注日,着工日,完工日,元請,元請支店,件名";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(order_id)) + "," +
               Utilities.escapeCsv(request_number) + "," +
               Utilities.escapeCsv(String.valueOf(order_date)) + "," +
               Utilities.escapeCsv(String.valueOf(start_date)) + "," +
               Utilities.escapeCsv(String.valueOf(end_date)) + "," +
               Utilities.escapeCsv(String.valueOf(prime_contractor_id)) + "," +
               Utilities.escapeCsv(String.valueOf(prime_contractor_office_id)) + "," +
               Utilities.escapeCsv(String.valueOf(title));
    }
}
