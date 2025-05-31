package com.kyouseipro.neo.entity.corporation;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

import com.kyouseipro.neo.interfaceis.Entity;
import com.kyouseipro.neo.service.DatabaseService;

import lombok.Data;

@Data
public class StaffEntity implements Entity {
    private int staff_id;
    private int company_id;
    private int office_id;
    private String company_name;
    private String office_name;
    private String name;
    private String name_kana;
    private String phone_number;
    private String email;
    private int version;
    private int state;

    private String user_name;

    @Override
    public void setEntity(ResultSet rs) {
        try {
            this.staff_id = rs.getInt("staff_id");
            this.company_id = rs.getInt("company_id");
            this.office_id = rs.getInt("office_id");
            this.company_name = rs.getString("company_name");
            this.office_name = rs.getString("office_name");
            this.name = rs.getString("name");
            this.name_kana = rs.getString("name_kana");
            this.phone_number = rs.getString("phone_number");
            this.email = rs.getString("email");
            this.version = rs.getInt("version");
            this.state = rs.getInt("state");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    // public String getInsertString() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append(logTable());
    //     sb.append("INSERT INTO staffs (");
    //     sb.append("company_id");
    //     sb.append(", office_id");
    //     sb.append(", name");
    //     sb.append(", name_kana");
    //     sb.append(", phone_number");
    //     sb.append(", email");
    //     sb.append(") ");
    //     sb.append(logString("作成"));
    //     sb.append(" VALUES (");
    //     sb.append(this.getCompany_id());
    //     sb.append(", " + this.getOffice_id());
    //     sb.append(", '" + this.getName() + "'");
    //     sb.append(", '" + this.getName_kana() + "'");
    //     sb.append(", '" + this.getPhone_number() + "'");
    //     sb.append(", '" + this.getEmail() + "'");
    //     sb.append(");");
    //     sb.append("DECLARE @NEW_ID int; SET @NEW_ID = @@IDENTITY;");
    //     // 変更履歴
    //     sb.append("INSERT INTO staffs_log SELECT * FROM @StaffTable;");
    //     // SimpleData
    //     sb.append(DatabaseService.getInsertLogTableString(this.getUser_name(), "staffs", "作成"));
    //     // sb.append("IF @NEW_ID > 0 BEGIN ");
    //     // sb.append(HistoryEntity.insertString(user_name, "staffs", "作成成功", "@NEW_ID", ""));
    //     // sb.append("SELECT @NEW_ID as number, '作成しました' as text; END");
    //     // sb.append(" ELSE BEGIN ");
    //     // sb.append(HistoryEntity.insertString(user_name, "staffs", "作成成功", "@NEW_ID", ""));
    //     // sb.append("SELECT 0 as number, '作成できませんでした' as text; END;");
    //     return sb.toString();
    // }

    // public String getUpdateString() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append(logTable());
    //     sb.append("UPDATE staffs SET");
    //     sb.append(" company_id = " + this.getCompany_id());
    //     sb.append(", office_id = " + this.getOffice_id());
    //     sb.append(", name = '" + this.getName() + "'");
    //     sb.append(", name_kana = '" + this.getName_kana() + "'");
    //     sb.append(", phone_number = '" + this.getPhone_number() + "'");
    //     sb.append(", email = '" + this.getEmail() + "'");
    //     sb.append(", update_date = '" + LocalDateTime.now() + "'");
    //     int ver = this.getVersion() + 1;
    //     sb.append(", version = " + ver);
    //     sb.append(logString("作成"));
    //     sb.append(" WHERE staff_id = " + this.getStaff_id() + " AND version = " + this.getVersion() + ";");
    //     sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
    //     // 変更履歴
    //     sb.append("INSERT INTO staffs_log SELECT * FROM @StaffTable;");
    //     // SimpleData
    //     sb.append(DatabaseService.getUpdateLogTableString(this.getUser_name(), "staffs", "変更"));
    //     // sb.append("IF @ROW_COUNT > 0 BEGIN ");
    //     // sb.append(HistoryEntity.insertString(user_name, "staffs", "変更成功", "@ROW_COUNT", ""));
    //     // sb.append("SELECT 200 as number, '変更しました' as text; END");
    //     // sb.append(" ELSE BEGIN ");
    //     // sb.append(HistoryEntity.insertString(user_name, "staffs", "変更失敗", "@ROW_COUNT", ""));
    //     // sb.append("SELECT 0 as number, '変更できませんでした' as text; END;");

    //     return sb.toString();
    // }

    // private String logTable() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("DECLARE @StaffTable TABLE (");
    //     sb.append("editor NVARCHAR(255)");
    //     sb.append(", process NVARCHAR(50)");
    //     sb.append(", log_regist_date DATETIME2(7)");
    //     sb.append(", staff_id INT");
    //     sb.append(", company_id INT");
    //     sb.append(", office_id INT");
    //     sb.append(", name NVARCHAR(255)");
    //     sb.append(", name_kana NVARCHAR(255)");
    //     sb.append(", phone_number NVARCHAR(15)");
    //     sb.append(", email NVARCHAR(255)");
    //     sb.append(", regist_date DATE");
    //     sb.append(", update_date DATE");
    //     sb.append(", version INT");
    //     sb.append(", state INT");
    //     sb.append(");");

    //     return sb.toString();
    // }
    // private String logString(String process) {
    //     StringBuilder sb = new StringBuilder();
    //     StringBuilder sb2 = new StringBuilder();
    //     sb.append(" OUTPUT");
    //     sb.append("'" + this.getUser_name() + "'");             sb2.append("editor");
    //     sb.append(", '" + process + "'");                       sb2.append(", process");
    //     sb.append(", CURRENT_TIMESTAMP");                   sb2.append(", log_regist_date");
    //     sb.append(", INSERTED.staff_id");                   sb2.append(", staff_id");
    //     sb.append(", INSERTED.company_id");                 sb2.append(", company_id");
    //     sb.append(", INSERTED.office_id");                  sb2.append(", office_id");
    //     sb.append(", INSERTED.name");                       sb2.append(", name");
    //     sb.append(", INSERTED.name_kana");                  sb2.append(", name_kana");
    //     sb.append(", INSERTED.phone_number");               sb2.append(", phone_number");
    //     sb.append(", INSERTED.email");                      sb2.append(", email");
    //     sb.append(", INSERTED.regist_date");                sb2.append(", regist_date");
    //     sb.append(", INSERTED.update_date");                sb2.append(", update_date");
    //     sb.append(", INSERTED.version");                    sb2.append(", version");
    //     sb.append(", INSERTED.state");                      sb2.append(", state");
    //     sb.append(" INTO @StaffTable (");                   sb2.append(")");
    //     sb.append(sb2.toString());
    //     return sb.toString();
    // }

    // public static String getCsvString(List<Entity> items) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("ID,");
    //     sb.append("担当者名,");
    //     sb.append("たんとうしゃめい,");
    //     sb.append("会社名,");
    //     sb.append("支店名,");
    //     sb.append("携帯番号,");
    //     sb.append("メールアドレス,");
    //     sb.append("\n");
    //     for (Entity item : items) {
    //         StaffEntity entity = (StaffEntity) item;
    //         sb.append(String.valueOf(entity.getStaff_id()) + ",");
    //         sb.append(entity.getName() + ",");
    //         sb.append(entity.getName_kana() + ",");
    //         sb.append(entity.getCompany_name() + ",");
    //         sb.append(entity.getOffice_name() + ",");
    //         sb.append(entity.getPhone_number() + ",");
    //         sb.append(entity.getEmail() + ",");
    //         sb.append("\n"); // 改行を追加
    //     }
    //     return sb.toString();
    // }
}
