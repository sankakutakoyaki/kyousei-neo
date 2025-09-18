package com.kyouseipro.neo.entity.personnel;

import java.time.LocalDate;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class EmployeeEntity implements CsvExportable {
    private int employee_id;
    private int company_id;
    private int office_id;
    private String account;
    private int code;
    private int category;
    private String last_name;
    private String first_name;
    private String full_name;
    private String last_name_kana;
    private String first_name_kana;
    private String full_name_kana;
    private String phone_number;
    private String postal_code;
    private String full_address;
    private String email;
    private int gender;
    private int blood_type;
    private LocalDate birthday = LocalDate.of(9999, 12, 31);
    private String emergency_contact;
    private String emergency_contact_number;
    private LocalDate date_of_hire;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,アカウント,コード,姓,名,せい,めい,携帯番号,郵便番号,住所,メールアドレス,性別,血液型,生年月日,緊急連絡先,緊急連絡先番号,入社日";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(employee_id)) + "," +
               Utilities.escapeCsv(account) + "," +
               Utilities.escapeCsv(String.valueOf(code)) + "," +
               Utilities.escapeCsv(last_name) + "," +
               Utilities.escapeCsv(first_name) + "," +
               Utilities.escapeCsv(last_name_kana) + "," +
               Utilities.escapeCsv(first_name_kana) + "," +
               Utilities.escapeCsv(phone_number) + "," +
               Utilities.escapeCsv(postal_code) + "," +
               Utilities.escapeCsv(full_address) + "," +
               Utilities.escapeCsv(email) + "," +
               Utilities.escapeCsv(Enums.gender.getDescriptionByNum(gender)) + "," +
               Utilities.escapeCsv(Enums.bloodType.getDescriptionByNum(blood_type)) + "," +
               Utilities.escapeCsv(String.valueOf(birthday)) +
               Utilities.escapeCsv(emergency_contact) + "," +
               Utilities.escapeCsv(emergency_contact_number) + "," +
               Utilities.escapeCsv(String.valueOf(date_of_hire));
    }

    // private String user_name;

    // @Override
    // public void setEntity(ResultSet rs) {
    //     try {
    //         this.employee_id = rs.getInt("employee_id");
    //         this.company_id = rs.getInt("company_id");
    //         this.office_id = rs.getInt("office_id");
    //         this.account = rs.getString("account");
    //         this.code = rs.getInt("code");
    //         this.category = rs.getInt("category");
    //         this.last_name = rs.getString("last_name");
    //         this.first_name = rs.getString("first_name");
    //         this.full_name = rs.getString("full_name");
    //         this.last_name_kana = rs.getString("last_name_kana");
    //         this.first_name_kana = rs.getString("first_name_kana");
    //         this.full_name_kana = rs.getString("full_name_kana");
    //         this.phone_number = rs.getString("phone_number");
    //         this.postal_code = rs.getString("postal_code");
    //         this.full_address = rs.getString("full_address");
    //         this.email = rs.getString("email");
    //         this.gender = rs.getInt("gender");
    //         this.blood_type = rs.getInt("blood_type");
    //         this.birthday = rs.getDate("birthday").toLocalDate();
    //         this.emergency_contact = rs.getString("emergency_contact");
    //         this.emergency_contact_number = rs.getString("emergency_contact_number");
    //         this.date_of_hire = rs.getDate("date_of_hire").toLocalDate();
    //         this.version = rs.getInt("version");
    //         this.state = rs.getInt("state");
    //     } catch (Exception e) {
    //         System.out.println(e);
    //     }
    // }
}
