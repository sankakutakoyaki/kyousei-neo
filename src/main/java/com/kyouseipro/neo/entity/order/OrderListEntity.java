package com.kyouseipro.neo.entity.order;

import java.time.LocalDate;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class OrderListEntity implements CsvExportable {
    private int orderId;
    private String requestNumber;
    private LocalDate startDate = LocalDate.of(9999, 12, 31);
    private LocalDate endDate = LocalDate.of(9999, 12, 31);
    private int primeConstractorId;
    private String primeConstractorName;
    private int primeConstractorOfficeId;
    private String primeConstractorOfficeName;
    private String title;
    private String orderPostalCode;
    private String orderFullAddress;

    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,依頼番号,着工日,完工日,元請,元請支店,件名";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(orderId)) + "," +
               Utilities.escapeCsv(requestNumber) + "," +
               Utilities.escapeCsv(String.valueOf(startDate)) + "," +
               Utilities.escapeCsv(String.valueOf(endDate)) + "," +
               Utilities.escapeCsv(primeConstractorName) + "," +
               Utilities.escapeCsv(primeConstractorOfficeName) + "," +
               Utilities.escapeCsv(String.valueOf(title)) + "," +
               Utilities.escapeCsv(String.valueOf(orderPostalCode)) + "," +
               Utilities.escapeCsv(String.valueOf(orderFullAddress));
    }
}