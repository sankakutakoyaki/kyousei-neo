package com.kyouseipro.neo.personnel.workingconditions.entity;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaces.CsvExportable;

import lombok.Data;

@Data
public class WorkingConditionsEntity implements CsvExportable {
    private int workingConditionsId;
    private int employeeId;
    private String officeName;
    private int code;
    private int category;
    private String fullName;
    private String fullNameKana;
    private int paymentMethod;
    private int payType;
    private int baseSalary;
    private int transCost;
    // private LocalTime basicStartTime = LocalTime.of(0, 0);
    // private LocalTime basicEndTime = LocalTime.of(0, 0);
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,コード,営業所,名前,かな,支払い方法,給与形態,基本給/時給,交通費";
    }
    
    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(employeeId) + "," +
               Utilities.escapeCsv(code) + "," +
               Utilities.escapeCsv(officeName) + "," +
               Utilities.escapeCsv(fullName) + "," +
               Utilities.escapeCsv(fullNameKana) + "," +
               Utilities.escapeCsv(Enums.paymentMethod.getDescriptionByNum(paymentMethod)) + "," +
               Utilities.escapeCsv(Enums.payType.getDescriptionByNum(payType)) + "," +
               Utilities.escapeCsv(baseSalary) + "," +
               Utilities.escapeCsv(transCost) + ",";
    }
}
