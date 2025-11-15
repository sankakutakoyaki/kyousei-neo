package com.kyouseipro.neo.entity.sales;

import java.time.LocalDate;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class OrderEntity implements CsvExportable {
    private int order_id;
    private String request_number;
    private LocalDate order_date = LocalDate.of(9999, 12, 31);
    private LocalDate start_date = LocalDate.of(9999, 12, 31);
    private LocalDate end_date = LocalDate.of(9999, 12, 31);
    private int prime_constractor_id;
    private String prime_constractor_name;
    private int prime_constractor_office_id;
    private String prime_constractor_office_name;
    private String title;
    private String order_postal_code;
    private String order_full_address;

    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,依頼番号,受注日,着工日,完工日,元請,元請支店,件名,郵便番号.住所";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(order_id)) + "," +
               Utilities.escapeCsv(request_number) + "," +
               Utilities.escapeCsv(String.valueOf(order_date)) + "," +
               Utilities.escapeCsv(String.valueOf(start_date)) + "," +
               Utilities.escapeCsv(String.valueOf(end_date)) + "," +
               Utilities.escapeCsv(String.valueOf(prime_constractor_name)) + "," +
               Utilities.escapeCsv(String.valueOf(prime_constractor_office_name)) + "," +
               Utilities.escapeCsv(String.valueOf(title)) + "," +
               Utilities.escapeCsv(String.valueOf(order_postal_code)) + "," +
               Utilities.escapeCsv(String.valueOf(order_full_address));
    }
}
