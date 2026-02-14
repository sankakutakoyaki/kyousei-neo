package com.kyouseipro.neo.ks.entity;

import java.time.LocalDate;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaces.CsvExportable;

import lombok.Data;

@Data
public class KsSalesEntity implements CsvExportable {
    private int ksSalesId;
    private String targetYearMonth;
    private int partnerStoreCode;
    private String partnerStoreName;
    private int corporationCode;
    private String corporationName;
    private int storeCode;
    private String storeName;
    private LocalDate slipDate;
    private int purchaseSlipNumber;
    private String purchaseSlipType;
    private String janCode;
    private String modelNumber;
    private int quantity;
    private int amount;
    private int deliveryPaymentMgmtNumber;
    private int salesSlipNumber;
    private int salesStoreCode;
    private String salesStoreName;
    private String staffCompany;
    private int staffCode1;
    private String staffName1;
    private int staffCode2;
    private String staffName2;
    private LocalDate deliveryDate;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,対象年月,協力店コード,協力店名,法人コード,法人名,店舗ｺｰﾄﾞ,店舗名,伝票日付,仕入伝票番号,仕入伝票種別,JANコード,型番,数量,金額,配送入出金管理番号,売上伝票番号,売上店舗コード,売上店舗名,担当者コード1,担当者名1,担当者コード2,担当者名2,配送日";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(ksSalesId) + "," +
               Utilities.escapeCsv(targetYearMonth) + ","  +
               Utilities.escapeCsv(partnerStoreCode) + "," +
               Utilities.escapeCsv(partnerStoreName) + "," +
               Utilities.escapeCsv(corporationCode) + "," +
               Utilities.escapeCsv(corporationName) + "," +
               Utilities.escapeCsv(storeCode) + "," +
               Utilities.escapeCsv(storeName) + "," +
               Utilities.escapeCsv(slipDate) + "," +
               Utilities.escapeCsv(purchaseSlipNumber) + "," +
               Utilities.escapeCsv(purchaseSlipType) + "," +
               Utilities.escapeCsv(janCode) + "," +
               Utilities.escapeCsv(modelNumber) + "," +
               Utilities.escapeCsv(quantity) + "," +
               Utilities.escapeCsv(amount) + "," +
               Utilities.escapeCsv(deliveryPaymentMgmtNumber) + "," +
               Utilities.escapeCsv(salesSlipNumber) + "," +
               Utilities.escapeCsv(salesStoreCode) + "," +
               Utilities.escapeCsv(salesStoreName) + "," +
               Utilities.escapeCsv(staffCode1) + "," +
               Utilities.escapeCsv(staffName1) + "," +
               Utilities.escapeCsv(staffCode2) + "," +
               Utilities.escapeCsv(staffName2) + "," +
               Utilities.escapeCsv(deliveryDate);
    }
}
