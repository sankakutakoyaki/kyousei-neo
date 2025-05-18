package com.kyouseipro.neo.entity.person;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.record.HistoryEntity;
import com.kyouseipro.neo.interfaceis.IEntity;

import lombok.Data;

@Data
public class EmployeeEntity implements IEntity {
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

    private String user_name;

    @Override
    public void setEntity(ResultSet rs) {
        try {
            this.employee_id = rs.getInt("employee_id");
            this.company_id = rs.getInt("company_id");
            this.office_id = rs.getInt("office_id");
            this.account = rs.getString("account");
            this.code = rs.getInt("code");
            this.category = rs.getInt("category");
            this.last_name = rs.getString("last_name");
            this.first_name = rs.getString("first_name");
            this.full_name = rs.getString("full_name");
            this.last_name_kana = rs.getString("last_name_kana");
            this.first_name_kana = rs.getString("first_name_kana");
            this.full_name_kana = rs.getString("full_name_kana");
            this.phone_number = rs.getString("phone_number");
            this.postal_code = rs.getString("postal_code");
            this.full_address = rs.getString("full_address");
            this.email = rs.getString("email");
            this.gender = rs.getInt("gender");
            this.blood_type = rs.getInt("blood_type");
            this.birthday = rs.getDate("birthday").toLocalDate();
            this.emergency_contact = rs.getString("emergency_contact");
            this.emergency_contact_number = rs.getString("emergency_contact_number");
            this.date_of_hire = rs.getDate("date_of_hire").toLocalDate();
            this.version = rs.getInt("version");
            this.state = rs.getInt("state");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getInsertString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        if (this.getBirthday() == null) {
            this.setBirthday(LocalDate.of(9999, 12, 31));
        }
        sb.append(logTable());
        sb.append("INSERT INTO employees (");
        sb.append("company_id");                sb2.append(this.getCompany_id());
        sb.append(", office_id");               sb2.append(", " + this.getOffice_id());
        sb.append(", account");                 sb2.append(", '" + this.getAccount() + "'");
        sb.append(", code");                    sb2.append(", " + this.getCode());
        sb.append(", category");                sb2.append(", " + this.getCategory());
        sb.append(", last_name");               sb2.append(", '" + this.getLast_name() + "'");
        sb.append(", first_name");              sb2.append(", '" + this.getFirst_name() + "'");
        sb.append(", last_name_kana");          sb2.append(", '" + this.getLast_name_kana() + "'");
        sb.append(", first_name_kana");         sb2.append(", '" + this.getFirst_name_kana() + "'");
        sb.append(", phone_number");            sb2.append(", '" + this.getPhone_number() + "'");
        sb.append(", postal_code");             sb2.append(", '" + this.getPostal_code() + "'");
        sb.append(", full_address");            sb2.append(", '" + this.getFull_address() + "'");
        sb.append(", email");                   sb2.append(", '" + this.getEmail() + "'");
        sb.append(", gender");                  sb2.append(", " + this.getGender());
        sb.append(", blood_type");              sb2.append(", " + this.getBlood_type());
        sb.append(", birthday");                sb2.append(", '" + this.getBirthday() + "'");
        sb.append(", emergency_contact");       sb2.append(", '" + this.getEmergency_contact() + "'");
        sb.append(", emergency_contact_number");sb2.append(", '" + this.getEmergency_contact_number() + "'");
        sb.append(", date_of_hire");            sb2.append(", '" + this.getDate_of_hire() + "'");
        sb.append(")");                         sb2.append(");");
        sb.append(logString("新規"));
        sb.append(" VALUES (");sb.append(sb2.toString());
        sb.append("DECLARE @NEW_ID int;SET @NEW_ID = @@IDENTITY;");
        // 変更履歴
        sb.append("INSERT INTO employees_log SELECT * FROM @EmployeeTable;");
        // SimpleData
        sb.append("IF @NEW_ID > 0 BEGIN ");
        sb.append(HistoryEntity.insertString(user_name, "employees", "作成成功", "@NEW_ID", ""));
        sb.append("SELECT @NEW_ID as number, '作成しました' as text; END");
        sb.append(" ELSE BEGIN ");
        sb.append(HistoryEntity.insertString(user_name, "employees", "作成失敗", "@NEW_ID", ""));
        sb.append("SELECT 0 as number, '作成できませんでした' as text; END;");
        return sb.toString();
    }

    public String getUpdateString() {
        StringBuilder sb = new StringBuilder();
        if (this.getBirthday() == null) {
            this.setBirthday(LocalDate.of(9999, 12, 31));
        }
        sb.append(logTable());
        sb.append("UPDATE employees SET");
        sb.append(" company_id = " + this.getCompany_id());
        sb.append(", office_id = " + this.getOffice_id());
        sb.append(", account = '" + this.getAccount() + "'");
        sb.append(", code = " + this.getCode());
        sb.append(", category = " + this.getCategory());
        sb.append(", last_name = '" + this.getLast_name() + "'");
        sb.append(", first_name = '" + this.getFirst_name() + "'");
        sb.append(", last_name_kana = '" + this.getLast_name_kana() + "'");
        sb.append(", first_name_kana = '" + this.getFirst_name_kana() + "'");
        sb.append(", phone_number = '" + this.getPhone_number() + "'");
        sb.append(", postal_code = '" + this.getPostal_code() + "'");
        sb.append(", full_address = '" + this.getFull_address() + "'");
        sb.append(", email = '" + this.getEmail() + "'");
        sb.append(", gender = " + this.getGender());
        sb.append(", blood_type = " + this.getBlood_type());
        sb.append(", birthday = '" + this.getBirthday() + "'");
        sb.append(", emergency_contact = '" + this.getEmergency_contact() + "'");
        sb.append(", emergency_contact_number = '" + this.getEmergency_contact_number() + "'");
        sb.append(", date_of_hire = '" + this.getDate_of_hire() + "'");
        sb.append(", update_date = '" + LocalDateTime.now() + "'");
        int ver = this.getVersion() + 1;
        sb.append(", version = " + ver);
        sb.append(logString("更新"));
        sb.append(" WHERE employee_id = " + this.getEmployee_id() + " AND version = " + this.getVersion() + ";");
        sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
        // 変更履歴
        sb.append("INSERT INTO employees_log SELECT * FROM @EmployeeTable;");
        // SimpleData
        sb.append("IF @ROW_COUNT > 0 BEGIN ");
        sb.append(HistoryEntity.insertString(user_name, "employees", "変更成功", "@ROW_COUNT", ""));
        sb.append("SELECT 200 as number, '変更しました' as text; END");
        sb.append(" ELSE BEGIN ");
        sb.append(HistoryEntity.insertString(user_name, "employees", "変更失敗", "@ROW_COUNT", ""));
        sb.append("SELECT 0 as number, '変更できませんでした' as text; END;");
        return sb.toString();
    }

    public String logTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("DECLARE @EmployeeTable TABLE (");
        sb.append("editor NVARCHAR(255)");
        sb.append(", process NVARCHAR(50)");
        sb.append(", log_regist_date DATETIME2(7)");
        sb.append(", employee_id INT");
        sb.append(", company_id INT");
        sb.append(", office_id INT");
        sb.append(", account NVARCHAR(255)");
        sb.append(", code INT");
        sb.append(", category INT");
        sb.append(", last_name NVARCHAR(255)");
        sb.append(", first_name NVARCHAR(255)");
        sb.append(", last_name_kana NVARCHAR(255)");
        sb.append(", first_name_kana NVARCHAR(255)");
        sb.append(", phone_number NVARCHAR(15)");
        sb.append(", postal_code NVARCHAR(8)");
        sb.append(", full_address NVARCHAR(255)");
        sb.append(", email NVARCHAR(255)");
        sb.append(", gender INT");
        sb.append(", blood_type INT");
        sb.append(", birthday DATE");
        sb.append(", emergency_contact NVARCHAR(255)");
        sb.append(", emergency_contact_number NVARCHAR(255)");
        sb.append(", date_of_hire DATE");
        sb.append(", regist_date DATE");
        sb.append(", update_date DATE");
        sb.append(", version INT");
        sb.append(", state INT");
        sb.append(");");
        return sb.toString();
    }

    public String logString(String process) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append(" OUTPUT");
        sb.append("'" + this.getUser_name() + "'");         sb2.append("editor");
        sb.append(", '" + process + "'");                   sb2.append(", process");
        sb.append(", CURRENT_TIMESTAMP");                   sb2.append(", log_regist_date");
        sb.append(", INSERTED.employee_id");                sb2.append(", employee_id");
        sb.append(", INSERTED.company_id");                 sb2.append(", company_id");
        sb.append(", INSERTED.office_id");                  sb2.append(", office_id");
        sb.append(", INSERTED.account");                    sb2.append(", account");
        sb.append(", INSERTED.code");                       sb2.append(", code");
        sb.append(", INSERTED.category");          sb2.append(", category");
        sb.append(", INSERTED.last_name");                  sb2.append(", last_name");
        sb.append(", INSERTED.first_name");                 sb2.append(", first_name");
        sb.append(", INSERTED.last_name_kana");             sb2.append(", last_name_kana");
        sb.append(", INSERTED.first_name_kana");            sb2.append(", first_name_kana");
        sb.append(", INSERTED.phone_number");               sb2.append(", phone_number");
        sb.append(", INSERTED.postal_code");                sb2.append(", postal_code");
        sb.append(", INSERTED.full_address");               sb2.append(", full_address");
        sb.append(", INSERTED.email");                      sb2.append(", email");
        sb.append(", INSERTED.gender");                     sb2.append(", gender");
        sb.append(", INSERTED.blood_type");                 sb2.append(", blood_type");
        sb.append(", INSERTED.birthday");                   sb2.append(", birthday");
        sb.append(", INSERTED.emergency_contact");          sb2.append(", emergency_contact");
        sb.append(", INSERTED.emergency_contact_number");   sb2.append(", emergency_contact_number");
        sb.append(", INSERTED.date_of_hire");               sb2.append(", date_of_hire");
        sb.append(", INSERTED.regist_date");                sb2.append(", regist_date");
        sb.append(", INSERTED.update_date");                sb2.append(", update_date");
        sb.append(", INSERTED.version");                    sb2.append(", version");
        sb.append(", INSERTED.state");                      sb2.append(", state");
        sb.append(" INTO @EmployeeTable (");                sb2.append(")");
        sb.append(sb2.toString());
        return sb.toString();
    }

    public static String getCsvString(List<IEntity> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,");
        sb.append("コード,");
        sb.append("アカウント,");
        sb.append("姓,");
        sb.append("名,");
        sb.append("せい,");
        sb.append("めい,");
        sb.append("携帯番号,");
        sb.append("郵便番号,");
        sb.append("住所,");
        sb.append("メールアドレス,");
        sb.append("性別,");
        sb.append("血液型,");
        sb.append("生年月日,");
        sb.append("緊急連絡先,");
        sb.append("緊急連絡先番号,");
        sb.append("\n");
        for (IEntity item : items) {
            EmployeeEntity entity = (EmployeeEntity) item;
            sb.append(String.valueOf(entity.getEmployee_id()) + ",");
            sb.append(String.valueOf(entity.getCode()) + ",");
            sb.append(entity.getAccount() + ",");
            sb.append(entity.getLast_name() + ",");
            sb.append(entity.getFirst_name() + ",");
            sb.append(entity.getLast_name_kana() + ",");
            sb.append(entity.getFirst_name_kana() + ",");
            sb.append(entity.getPhone_number() + ",");
            sb.append(entity.getPostal_code() + ",");
            sb.append(entity.getFull_address() + ",");
            sb.append(entity.getEmail() + ",");
            sb.append(Enums.gender.getStrByNum(entity.getGender()) + ",");
            sb.append(Enums.bloodType.getStrByNum(entity.getBlood_type()) + ",");
            sb.append(entity.getBirthday() + ",");
            sb.append(entity.getEmergency_contact() + ",");
            sb.append(entity.getEmergency_contact_number() + ",");
            sb.append("\n"); // 改行を追加
        }
        return sb.toString();
    }
}
