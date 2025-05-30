package com.kyouseipro.neo.entity.person;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.interfaceis.Entity;
import com.kyouseipro.neo.service.DatabaseService;

import lombok.Data;

@Data
public class WorkingConditionsEntity implements Entity {
    private int working_conditions_id;
    private int employee_id;
    private String office_name;
    private int code;
    private int category;
    private String full_name;
    private String full_name_kana;
    private int payment_method;
    private int pay_type;
    private int base_salary;
    private int trans_cost;
    private LocalTime basic_start_time = LocalTime.of(0, 0);
    private LocalTime basic_end_time = LocalTime.of(0, 0);
    private int version;
    private int state;

    private String user_name;

    @Override
    public void setEntity(ResultSet rs) {
        try {
            this.working_conditions_id = rs.getInt("working_conditions_id");
            this.employee_id = rs.getInt("employee_id");
            this.office_name = rs.getString("office_name");
            this.code = rs.getInt("code");
            this.category = rs.getInt("category");
            this.full_name = rs.getString("full_name");
            this.full_name_kana = rs.getString("full_name_kana");
            this.payment_method = rs.getInt("payment_method");
            this.pay_type = rs.getInt("pay_type");
            this.base_salary = rs.getInt("base_salary");
            this.trans_cost = rs.getInt("trans_cost");
            this.basic_start_time = rs.getTime("basic_start_time").toLocalTime();
            this.basic_end_time = rs.getTime("basic_end_time").toLocalTime();
            this.version = rs.getInt("version");
            this.state = rs.getInt("state");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * セレクト用基本文字列
     * @return
     */
    public static String selectString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e.employee_id, e.full_name, e.full_name_kana, e.code, e.category");
        sb.append(", w.working_conditions_id, w.payment_method, w.pay_type, w.base_salary, w.trans_cost, w.version, w.state");
        sb.append(", ISNULL(NULLIF(w.basic_start_time, ''), '00:00:00') as basic_start_time, ISNULL(NULLIF(w.basic_end_time, ''), '00:00:00') as basic_end_time");
        sb.append(", ISNULL(NULLIF(o.name, ''), '登録なし') as office_name FROM employees e");
        sb.append(" LEFT OUTER JOIN working_conditions w ON w.employee_id = e.employee_id AND NOT (w.state = " + Enums.state.DELETE.getNum() + ")");
        sb.append(" LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = " + Enums.state.DELETE.getNum() + ")");
        return sb.toString();
    }

    public String getInsertString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append(logTable());
        sb.append("INSERT INTO working_conditions (");
        sb.append("employee_id");               sb2.append(this.getEmployee_id());
        sb.append(", payment_method");          sb2.append(", " + this.getPayment_method());
        sb.append(", pay_type");                sb2.append(", " + this.getPay_type());
        sb.append(", base_salary");             sb2.append(", " + this.getBase_salary());
        sb.append(", trans_cost");              sb2.append(", " + this.getTrans_cost());
        sb.append(", basic_start_time");        sb2.append(", '" + this.getBasic_start_time() + "'");
        sb.append(", basic_end_time");          sb2.append(", '" + this.getBasic_end_time() + "'");
        sb.append(")");                         sb2.append(");");
        sb.append(logString("新規"));
        sb.append(" VALUES (");sb.append(sb2.toString());
        sb.append("DECLARE @NEW_ID int;SET @NEW_ID = @@IDENTITY;");
        // 変更履歴
        sb.append("INSERT INTO working_conditions_log SELECT * FROM @WorkingConditionsTable;");
        // SimpleData
        sb.append(DatabaseService.getInsertLogTableString(this.getUser_name(), "working_conditions", "作成"));
        // sb.append("IF @NEW_ID > 0 BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "working_conditons", "作成成功", "@NEW_ID", ""));
        // sb.append("SELECT @NEW_ID as number, '作成しました' as text; END");
        // sb.append(" ELSE BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "working_conditons", "作成失敗", "@NEW_ID", ""));
        // sb.append("SELECT 0 as number, '作成できませんでした' as text; END;");
        return sb.toString();
    }

    public String getUpdateString() {
        StringBuilder sb = new StringBuilder();
        sb.append(logTable());
        sb.append("UPDATE working_conditions SET");
        sb.append(" employee_id = " + this.getEmployee_id());
        sb.append(", payment_method = " + this.getPayment_method());
        sb.append(", pay_type = " + this.getPay_type());
        sb.append(", base_salary = " + this.getBase_salary());
        sb.append(", trans_cost = " + this.getTrans_cost());
        sb.append(", basic_start_time = '" + this.getBasic_start_time() + "'");
        sb.append(", basic_end_time = '" + this.getBasic_end_time() + "'");
        sb.append(", update_date = '" + LocalDateTime.now() + "'");
        int ver = this.getVersion() + 1;
        sb.append(", version = " + ver);
        sb.append(logString("更新"));
        sb.append(" WHERE working_conditions_id = " + this.getWorking_conditions_id() + " AND version = " + this.getVersion() + ";");
        sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
        // 変更履歴
        sb.append("INSERT INTO working_conditions_log SELECT * FROM @WorkingConditionsTable;");
        // SimpleData
        sb.append(DatabaseService.getUpdateLogTableString(this.getUser_name(), "working_conditions", "変更"));
        // sb.append("IF @ROW_COUNT > 0 BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "working_conditons", "変更成功", "@ROW_COUNT", ""));
        // sb.append("SELECT 200 as number, '変更しました' as text; END");
        // sb.append(" ELSE BEGIN ");
        // sb.append(HistoryEntity.insertString(user_name, "working_conditons", "変更失敗", "@ROW_COUNT", ""));
        // sb.append("SELECT 0 as number, '変更できませんでした' as text; END;");
        return sb.toString();
    }

    public String logTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("DECLARE @WorkingConditionsTable TABLE (");
        sb.append("editor NVARCHAR(255)");
        sb.append(", process NVARCHAR(50)");
        sb.append(", log_regist_date DATETIME2(7)");
        sb.append(", working_conditions_id INT");
        sb.append(", employee_id INT");
        sb.append(", payment_method INT");
        sb.append(", pay_type INT");
        sb.append(", base_salary INT");
        sb.append(", trans_cost INT");
        sb.append(", basic_start_time TIME(7)");
        sb.append(", basic_end_time TIME(7)");
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
        sb.append(", INSERTED.working_conditions_id");      sb2.append(", working_conditions_id");
        sb.append(", INSERTED.employee_id");                sb2.append(", employee_id");
        sb.append(", INSERTED.payment_method");             sb2.append(", payment_method");
        sb.append(", INSERTED.pay_type");                   sb2.append(", pay_type");
        sb.append(", INSERTED.base_salary");                sb2.append(", base_salary");
        sb.append(", INSERTED.trans_cost");                 sb2.append(", trans_cost");
        sb.append(", INSERTED.basic_start_time");           sb2.append(", basic_start_time");
        sb.append(", INSERTED.basic_end_time");             sb2.append(", basic_end_time");
        sb.append(", INSERTED.regist_date");                sb2.append(", regist_date");
        sb.append(", INSERTED.update_date");                sb2.append(", update_date");
        sb.append(", INSERTED.version");                    sb2.append(", version");
        sb.append(", INSERTED.state");                      sb2.append(", state");
        sb.append(" INTO @WorkingConditionsTable (");                sb2.append(")");
        sb.append(sb2.toString());
        return sb.toString();
    }

    public static String getCsvString(List<Entity> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,");
        sb.append("コード,");
        sb.append("営業所,");
        sb.append("名前,");
        sb.append("かな,");
        sb.append("支払い方法,");
        sb.append("給与形態,");
        sb.append("基本給/時給,");
        sb.append("交通費,");
        sb.append("基本始業時刻,");
        sb.append("基本就業時刻,");
        sb.append("\n");
        for (Entity item : items) {
            WorkingConditionsEntity entity = (WorkingConditionsEntity) item;
            sb.append(String.valueOf(entity.getEmployee_id()) + ",");
            sb.append(String.valueOf(entity.getCode()) + ",");
            sb.append(entity.getOffice_name() + ",");
            sb.append(entity.getFull_name() + ",");
            sb.append(entity.getFull_name_kana() + ",");
            sb.append(entity.getPayment_method() + ",");
            sb.append(entity.getPay_type() + ",");
            sb.append(entity.getBase_salary() + ",");
            sb.append(entity.getTrans_cost() + ",");
            sb.append(entity.getBasic_start_time() + ",");
            sb.append(entity.getBasic_end_time() + ",");
            sb.append("\n"); // 改行を追加
        }
        return sb.toString();
    }
}
