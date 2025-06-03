package com.kyouseipro.neo.entity.corporation;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class OfficeEntity implements CsvExportable {
    private int office_id;
    private int company_id;
    private String company_name;
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
        return "ID,支店名,してんめい,電話番号,FAX番号,郵便番号,住所,メールアドレス,WEBアドレス";
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

        // public static String getCsvString(List<Entity> items) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("ID,");
    //     sb.append("会社名,");
    //     sb.append("支店名,");
    //     sb.append("してんめい,");
    //     sb.append("電話番号,");
    //     sb.append("FAX番号,");
    //     sb.append("郵便番号,");
    //     sb.append("住所,");
    //     sb.append("メールアドレス,");
    //     sb.append("WEBアドレス,");
    //     sb.append("\n");
    //     for (Entity item : items) {
    //         OfficeEntity entity = (OfficeEntity) item;
    //         sb.append(String.valueOf(entity.getOffice_id()) + ",");
    //         sb.append(entity.getCompany_name() + ",");
    //         sb.append(entity.getName() + ",");
    //         sb.append(entity.getName_kana() + ",");
    //         sb.append(entity.getTel_number() + ",");
    //         sb.append(entity.getFax_number() + ",");
    //         sb.append(entity.getPostal_code() + ",");
    //         sb.append(entity.getFull_address() + ",");
    //         sb.append(entity.getEmail() + ",");
    //         sb.append(entity.getWeb_address() + ",");
    //         sb.append("\n"); // 改行を追加
    //     }
    //     return sb.toString();
    // }

    // private String user_name;

    // @Override
    // public void setEntity(ResultSet rs) {
    //     try {
    //         this.office_id = rs.getInt("office_id");
    //         this.company_id = rs.getInt("company_id");
    //         this.company_name = rs.getString("company_name");
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
    
}
