package com.kyouseipro.neo.entity.person;

import java.sql.ResultSet;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.interfaceis.Entity;

import lombok.Data;

@Data
public class WorkingConditionsListEntity implements Entity {
    private int working_conditions_id;
    private int employee_id;
    private int category;
    private String full_name;
    private String full_name_kana;
    private String office_name;
    private boolean exist = false;

    private String user_name;

    @Override
    public void setEntity(ResultSet rs) {
        try {
            int id = rs.getInt("working_conditions_id");
            this.working_conditions_id = id;
            this.employee_id = rs.getInt("employee_id");
            this.category = rs.getInt("category");
            this.full_name = rs.getString("full_name");
            this.full_name_kana = rs.getString("full_name_kana");
            this.office_name = rs.getString("office_name");
            if (id > 0) this.exist = true;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * リストセレクト用基本文字列
     * @return
     */
    public static String selectString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e.employee_id, e.full_name, e.full_name_kana, e.category, e.state, w.working_conditions_id");
        sb.append(", ISNULL(NULLIF(o.name, ''), '登録なし') as office_name FROM employees e");
        sb.append(" LEFT OUTER JOIN working_conditions w ON w.employee_id = e.employee_id AND NOT (w.state = " + Enums.state.DELETE.getNum() + ")");
        sb.append(" LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = " + Enums.state.DELETE.getNum() + ")");
        return sb.toString();
    }
}