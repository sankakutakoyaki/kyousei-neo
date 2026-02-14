package com.kyouseipro.neo.corporation.company.entity;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaces.CsvExportable;

import lombok.Data;

@Data
public class CompanyEntity implements CsvExportable {
    private int companyId;
    private int category;
    private String name;
    private String nameKana;
    private String telNumber;
    private String faxNumber;
    private String postalCode;
    private String fullAddress;
    private String email;
    private String webAddress;
    private int isOriginalPrice;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,会社名,かいしゃめい,電話番号,FAX番号,郵便番号,住所,メールアドレス,WEBアドレス";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(companyId) + "," +
               Utilities.escapeCsv(name) + "," +
               Utilities.escapeCsv(nameKana) + "," +
               Utilities.escapeCsv(telNumber) + "," +
               Utilities.escapeCsv(faxNumber) + "," +
               Utilities.escapeCsv(postalCode) + "," +
               Utilities.escapeCsv(fullAddress) + "," +
               Utilities.escapeCsv(email) + "," +
               Utilities.escapeCsv(webAddress);
    }
}