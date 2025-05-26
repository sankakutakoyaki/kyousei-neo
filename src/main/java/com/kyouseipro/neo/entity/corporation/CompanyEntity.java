package com.kyouseipro.neo.entity.corporation;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

import com.kyouseipro.neo.entity.record.HistoryEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.service.DatabaseService;

import lombok.Data;

@Data
public class CompanyEntity implements IEntity {
    
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

    private String user_name;

    @Override
    public void setEntity(ResultSet rs) {
        try {
            this.company_id = rs.getInt("company_id");
            this.category = rs.getInt("category");
            this.name = rs.getString("name");
            this.name_kana = rs.getString("name_kana");
            this.tel_number = rs.getString("tel_number");
            this.fax_number = rs.getString("fax_number");
            this.postal_code = rs.getString("postal_code");
            this.full_address = rs.getString("full_address");
            this.email = rs.getString("email");
            this.web_address = rs.getString("web_address");
            this.version = rs.getInt("version");
            this.state = rs.getInt("state");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public String getInsertString() {
        StringBuilder sb = new StringBuilder();
        sb.append(logTable());
        sb.append("INSERT INTO companies (");
        sb.append("category");
        sb.append(", name");
        sb.append(", name_kana");
        sb.append(", tel_number");
        sb.append(", fax_number");
        sb.append(", postal_code");
        sb.append(", full_address");
        sb.append(", email");
        sb.append(", web_address");
        sb.append(") ");
        sb.append(logString("作成"));
        sb.append(" VALUES (");
        sb.append(this.getCategory());
        sb.append(", '" + this.getName() + "'");
        sb.append(", '" + this.getName_kana() + "'");
        sb.append(", '" + this.getTel_number() + "'");
        sb.append(", '" + this.getFax_number() + "'");
        sb.append(", '" + this.getPostal_code() + "'");
        sb.append(", '" + this.getFull_address() + "'");
        sb.append(", '" + this.getEmail() + "'");
        sb.append(", '" + this.getWeb_address() + "'");
        sb.append(");");
        sb.append("DECLARE @NEW_ID int; SET @NEW_ID = @@IDENTITY;");
        // 変更履歴
        sb.append("INSERT INTO companies_log SELECT * FROM @CompanyTable;");
        
        // SimpleData
        sb.append(DatabaseService.getInsertLogTableString(this.getUser_name(), "companies", "作成"));
        // sb.append("IF @NEW_ID > 0 BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "companies", "作成成功", "@NEW_ID", ""));
        // sb.append("SELECT @NEW_ID as number, '作成しました' as text; END");
        // sb.append(" ELSE BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "companies", "作成失敗い", "@NEW_ID", ""));
        // sb.append("SELECT 0 as number, '作成できませんでした' as text; END;");
        return sb.toString();
    }
    public String getUpdateString() {
        StringBuilder sb = new StringBuilder();
        sb.append(logTable());
        sb.append("UPDATE companies SET");
        sb.append(" category = " + this.getCategory());
        sb.append(", name = '" + this.getName() + "'");
        sb.append(", name_kana = '" + this.getName_kana() + "'");
        sb.append(", tel_number = '" + this.getTel_number() + "'");
        sb.append(", fax_number = '" + this.getFax_number() + "'");
        sb.append(", postal_code = '" + this.getPostal_code() + "'");
        sb.append(", full_address = '" + this.getFull_address() + "'");
        sb.append(", email = '" + this.getEmail() + "'");
        sb.append(", web_address = '" + this.getWeb_address() + "'");
        sb.append(", update_date = '" + LocalDateTime.now() + "'");
        int ver = this.getVersion() + 1;
        sb.append(", version = " + ver);
        sb.append(logString("作成"));
        sb.append(" WHERE company_id = " + this.getCompany_id() + " AND version = " + this.getVersion() + ";");
        sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
        // 変更履歴
        sb.append("INSERT INTO companies_log SELECT * FROM @CompanyTable;");
        // SimpleData
        sb.append(DatabaseService.getUpdateLogTableString(this.getUser_name(), "companies", "変更"));
        // sb.append("IF @ROW_COUNT > 0 BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "companies", "変更成功", "@ROW_COUNT", ""));
        // sb.append("SELECT 200 as number, '変更しました' as text; END");
        // sb.append(" ELSE BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "companies", "変更失敗", "@ROW_COUNT", ""));
        // sb.append("SELECT 0 as number, '変更できませんでした' as text; END;");

        return sb.toString();
    }
    private String logTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("DECLARE @CompanyTable TABLE (");
        sb.append("editor NVARCHAR(255)");
        sb.append(", process NVARCHAR(50)");
        sb.append(", log_regist_date DATETIME2(7)");
        sb.append(", company_id INT");
        sb.append(", category INT");
        sb.append(", name NVARCHAR(255)");
        sb.append(", name_kana NVARCHAR(255)");
        sb.append(", tel_number NVARCHAR(15)");
        sb.append(", fax_number NVARCHAR(15)");
        sb.append(", postal_code NVARCHAR(8)");
        sb.append(", full_address NVARCHAR(255)");
        sb.append(", email NVARCHAR(255)");
        sb.append(", web_address NVARCHAR(255)");
        sb.append(", regist_date DATE");
        sb.append(", update_date DATE");
        sb.append(", version INT");
        sb.append(", state INT");
        sb.append(");");

        return sb.toString();
    }
    private String logString(String process) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append(" OUTPUT");
        sb.append("'" + this.getUser_name() + "'");         sb2.append("editor");
        sb.append(", '" + process + "'");                   sb2.append(", process");
        sb.append(", CURRENT_TIMESTAMP");                   sb2.append(", log_regist_date");
        sb.append(", INSERTED.company_id");                 sb2.append(", company_id");
        sb.append(", INSERTED.category");                   sb2.append(", category");
        sb.append(", INSERTED.name");                       sb2.append(", name");
        sb.append(", INSERTED.name_kana");                  sb2.append(", name_kana");
        sb.append(", INSERTED.tel_number");                 sb2.append(", tel_number");
        sb.append(", INSERTED.fax_number");                 sb2.append(", fax_number");
        sb.append(", INSERTED.postal_code");                sb2.append(", postal_code");
        sb.append(", INSERTED.full_address");               sb2.append(", full_address");
        sb.append(", INSERTED.email");                      sb2.append(", email");
        sb.append(", INSERTED.web_address");                sb2.append(", web_address");
        sb.append(", INSERTED.regist_date");                sb2.append(", regist_date");
        sb.append(", INSERTED.update_date");                sb2.append(", update_date");
        sb.append(", INSERTED.version");                    sb2.append(", version");
        sb.append(", INSERTED.state");                      sb2.append(", state");
        sb.append(" INTO @CompanyTable (");                 sb2.append(")");
        sb.append(sb2.toString());
        return sb.toString();
    }

    public static String getCsvString(List<IEntity> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,");
        sb.append("会社名,");
        sb.append("かいしゃめい,");
        sb.append("電話番号,");
        sb.append("FAX番号,");
        sb.append("郵便番号,");
        sb.append("住所,");
        sb.append("メールアドレス,");
        sb.append("WEBアドレス,");
        sb.append("\n");
        for (IEntity item : items) {
            CompanyEntity entity = (CompanyEntity) item;
            sb.append(String.valueOf(entity.getCompany_id()) + ",");
            sb.append(entity.getName() + ",");
            sb.append(entity.getName_kana() + ",");
            sb.append(entity.getTel_number() + ",");
            sb.append(entity.getFax_number() + ",");
            sb.append(entity.getPostal_code() + ",");
            sb.append(entity.getFull_address() + ",");
            sb.append(entity.getEmail() + ",");
            sb.append(entity.getWeb_address() + ",");
            sb.append("\n"); // 改行を追加
        }
        return sb.toString();
    }
}