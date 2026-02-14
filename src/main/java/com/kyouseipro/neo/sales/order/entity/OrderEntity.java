package com.kyouseipro.neo.sales.order.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaces.CsvExportable;

import lombok.Data;

@Data
public class OrderEntity implements CsvExportable {
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
    private String contactInformation;
    private String contactInformation2;
    private String remarks;

    private List<OrderItemEntity> itemList = new ArrayList();
    private List<DeliveryStaffEntity> staffList = new ArrayList();
    private List<WorkContentEntity> workList = new ArrayList<>();

    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,依頼番号,受注日,着工日,完工日,元請,元請支店,件名,郵便番号,住所,連絡先,連絡先2,備考";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(orderId) + "," +
               Utilities.escapeCsv(requestNumber) + "," +
               Utilities.escapeCsv(startDate) + "," +
               Utilities.escapeCsv(endDate) + "," +
               Utilities.escapeCsv(primeConstractorName) + "," +
               Utilities.escapeCsv(primeConstractorOfficeName) + "," +
               Utilities.escapeCsv(title) + "," +
               Utilities.escapeCsv(orderPostalCode) + "," +
               Utilities.escapeCsv(orderFullAddress) + "," +
               Utilities.escapeCsv(contactInformation) + "," +
               Utilities.escapeCsv(contactInformation2) + "," +
               Utilities.escapeCsv(remarks);
    }
}
