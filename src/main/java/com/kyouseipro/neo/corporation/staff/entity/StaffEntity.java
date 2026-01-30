package com.kyouseipro.neo.corporation.staff.entity;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaces.CsvExportable;

import lombok.Data;

@Data
public class StaffEntity implements CsvExportable{
    private int staffId;
    private int companyId;
    private int officeId;
    private String companyName;
    private String officeName;
    private String name;
    private String nameKana;
    private String phoneNumber;
    private String email;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,担当者,たんとうしゃめい,会社名,支店名,携帯番号,メールアドレス";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(staffId)) + "," +
               Utilities.escapeCsv(name) + "," +
               Utilities.escapeCsv(nameKana) + "," +
               Utilities.escapeCsv(companyName) + "," +
               Utilities.escapeCsv(officeName) + "," +
               Utilities.escapeCsv(phoneNumber) + "," +
               Utilities.escapeCsv(email) + ",";
    }
}
