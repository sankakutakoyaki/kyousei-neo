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

    // /**
    //  * セレクト用基本文字列
    //  * @return
    //  */
    // public static String selectString() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT e.employee_id, e.full_name, e.full_name_kana, e.code, e.category");
    //     sb.append(", w.working_conditions_id, w.payment_method, w.pay_type, w.base_salary, w.trans_cost, w.version, w.state");
    //     sb.append(", ISNULL(NULLIF(w.basic_start_time, ''), '00:00:00') as basic_start_time, ISNULL(NULLIF(w.basic_end_time, ''), '00:00:00') as basic_end_time");
    //     sb.append(", ISNULL(NULLIF(o.name, ''), '登録なし') as office_name FROM employees e");
    //     sb.append(" LEFT OUTER JOIN working_conditions w ON w.employee_id = e.employee_id AND NOT (w.state = " + Enums.state.DELETE.getNum() + ")");
    //     sb.append(" LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = " + Enums.state.DELETE.getNum() + ")");
    //     return sb.toString();
    // }


    // public static String getCsvString(List<Entity> items) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("ID,");
    //     sb.append("コード,");
    //     sb.append("営業所,");
    //     sb.append("名前,");
    //     sb.append("かな,");
    //     sb.append("支払い方法,");
    //     sb.append("給与形態,");
    //     sb.append("基本給/時給,");
    //     sb.append("交通費,");
    //     sb.append("基本始業時刻,");
    //     sb.append("基本就業時刻,");
    //     sb.append("\n");
    //     for (Entity item : items) {
    //         WorkingConditionsEntity entity = (WorkingConditionsEntity) item;
    //         sb.append(String.valueOf(entity.getEmployee_id()) + ",");
    //         sb.append(String.valueOf(entity.getCode()) + ",");
    //         sb.append(entity.getOffice_name() + ",");
    //         sb.append(entity.getFull_name() + ",");
    //         sb.append(entity.getFull_name_kana() + ",");
    //         sb.append(entity.getPayment_method() + ",");
    //         sb.append(entity.getPay_type() + ",");
    //         sb.append(entity.getBase_salary() + ",");
    //         sb.append(entity.getTrans_cost() + ",");
    //         sb.append(entity.getBasic_start_time() + ",");
    //         sb.append(entity.getBasic_end_time() + ",");
    //         sb.append("\n"); // 改行を追加
    //     }
    //     return sb.toString();
    // }
}
