package com.kyouseipro.neo.entity.corporation;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class CompanyEntity implements CsvExportable {
    private int company_id;
    private int category;
    private String name;
    private String name_kana;
    private String tel_number;
    private String fax_number;
    private String postal_code;
    private String full_address;
    private String email;
    private String web_address;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,会社名,かいしゃめい,電話番号,FAX番号,郵便番号,住所,メールアドレス,WEBアドレス";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(company_id)) + "," +
               Utilities.escapeCsv(name) + "," +
               Utilities.escapeCsv(name_kana) + "," +
               Utilities.escapeCsv(tel_number) + "," +
               Utilities.escapeCsv(fax_number) + "," +
               Utilities.escapeCsv(postal_code) + "," +
               Utilities.escapeCsv(full_address) + "," +
               Utilities.escapeCsv(email) + "," +
               Utilities.escapeCsv(web_address);
    }

    // private String user_name;

    // @Override
    // public void setEntity(ResultSet rs) {
    //     try {
    //         this.company_id = rs.getInt("company_id");
    //         this.category = rs.getInt("category");
    //         this.name = rs.getString("name");
    //         this.name_kana = rs.getString("name_kana");
    //         this.tel_number = rs.getString("tel_number");
    //         this.fax_number = rs.getString("fax_number");
    //         this.postal_code = rs.getString("postal_code");
    //         this.full_address = rs.getString("full_address");
    //         this.email = rs.getString("email");
    //         this.web_address = rs.getString("web_address");
    //         this.version = rs.getInt("version");
    //         this.state = rs.getInt("state");
    //     } catch (Exception e) {
    //         System.out.println(e);
    //     }
    // }

    // public static CompanyEntity from(ResultSet rs) {
    //     CompanyEntity entity = new CompanyEntity();
    //     entity.setEntity(rs);
    //     return entity;
    // }
}