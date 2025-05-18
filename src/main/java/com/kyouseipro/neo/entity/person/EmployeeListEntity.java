package com.kyouseipro.neo.entity.person;

import java.sql.ResultSet;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.interfaceis.IEntity;

import lombok.Data;

@Data
public class EmployeeListEntity implements IEntity {
    private int employee_id;
    private int code;
    private String company_name;
    private String office_name;
    private String full_name;
    private String full_name_kana;
    private String phone_number;
    private int category;
    private String category_name;

    private String user_name;

    @Override
    public void setEntity(ResultSet rs) {
        try {
            this.employee_id = rs.getInt("employee_id");
            this.code = rs.getInt("code");
            this.company_name = rs.getString("company_name");
            this.office_name = rs.getString("office_name");
            this.full_name = rs.getString("full_name");
            this.full_name_kana = rs.getString("full_name_kana");
            String phone = rs.getString("phone_number");
            this.phone_number = phone.isEmpty() ? null: phone;
            int category = rs.getInt("category");
            this.category = category;
            this.category_name = category == 0 ? null: Enums.employeeCategory.getStrByNum(category);
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
        sb.append("SELECT e.employee_id, e.code, e.full_name, e.full_name_kana, e.category, e.phone_number");
        sb.append(", COALESCE(c.name, '') as company_name, COALESCE(o.name, '') as office_name FROM employees e");
        // sb.append(", COALESCE(c.name_kana, '') as company_name_kana, COALESCE(o.name_kana, '') as office_name_kana FROM employees e");
        sb.append(" LEFT OUTER JOIN companies c ON c.company_id = e.company_id AND NOT (c.state = " + Enums.state.DELETE.getNum() + ")");
        sb.append(" LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = " + Enums.state.DELETE.getNum() + ")");
        return sb.toString();
    }
}
