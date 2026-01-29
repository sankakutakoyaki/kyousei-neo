package com.kyouseipro.neo.entity.corporation;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class OfficeEntity implements CsvExportable {
    private int officeId;
    private int companyId;
    private String companyName;
    private String name;
    private String nameKana;
    private String telNumber;
    private String faxNumber;
    private String postalCode;
    private String fullAddress;
    private String email;
    private String webAddress;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,支店名,してんめい,電話番号,FAX番号,郵便番号,住所,メールアドレス,WEBアドレス";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(companyId)) + "," +
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
