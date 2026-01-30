package com.kyouseipro.neo.personnel.employee.entity;

import java.time.LocalDate;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaces.CsvExportable;

import lombok.Data;

@Data
public class EmployeeEntity implements CsvExportable {
    private int employeeId;
    private int companyId;
    private String companyName;
    private int officeId;
    private String officeName;
    private String account;
    private int code;
    private int category;
    private String lastName;
    private String firstName;
    private String fullName;
    private String lastNameKana;
    private String firstNameKana;
    private String fullNameKana;
    private String phoneNumber;
    private String postalCode;
    private String fullAddress;
    private String email;
    private int gender;
    private int bloodType;
    private LocalDate birthday = LocalDate.of(9999, 12, 31);
    private String emergencyContact;
    private String emergencyContactNumber;
    private LocalDate dateOfHire;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,アカウント,コード,姓,名,せい,めい,携帯番号,郵便番号,住所,メールアドレス,性別,血液型,生年月日,緊急連絡先,緊急連絡先番号,入社日";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(employeeId)) + "," +
               Utilities.escapeCsv(account) + "," +
               Utilities.escapeCsv(String.valueOf(code)) + "," +
               Utilities.escapeCsv(lastName) + "," +
               Utilities.escapeCsv(firstName) + "," +
               Utilities.escapeCsv(lastNameKana) + "," +
               Utilities.escapeCsv(firstNameKana) + "," +
               Utilities.escapeCsv(phoneNumber) + "," +
               Utilities.escapeCsv(postalCode) + "," +
               Utilities.escapeCsv(fullAddress) + "," +
               Utilities.escapeCsv(email) + "," +
               Utilities.escapeCsv(Enums.gender.getDescriptionByNum(gender)) + "," +
               Utilities.escapeCsv(Enums.bloodType.getDescriptionByNum(bloodType)) + "," +
               Utilities.escapeCsv(String.valueOf(birthday)) +
               Utilities.escapeCsv(emergencyContact) + "," +
               Utilities.escapeCsv(emergencyContactNumber) + "," +
               Utilities.escapeCsv(String.valueOf(dateOfHire));
    }
}
