package com.kyouseipro.neo.entity.order;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class DeliveryStaffEntity implements CsvExportable{
    private int deliveryStaffId;
    private int orderId;
    private int employeeId;
    private int companyId;
    private String companyName;
    private int officeId;
    private String officeName;
    private String fullName;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,受注番号,会社名,支店名,担当者名";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(deliveryStaffId)) + "," +
               Utilities.escapeCsv(String.valueOf(orderId)) + "," +
               Utilities.escapeCsv(companyName) + "," +
               Utilities.escapeCsv(officeName) + "," +
               Utilities.escapeCsv(fullName) + ",";
    }
}
