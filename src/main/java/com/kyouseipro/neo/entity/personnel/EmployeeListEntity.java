package com.kyouseipro.neo.entity.personnel;

import lombok.Data;

@Data
public class EmployeeListEntity {
    private int employee_id;
    private int code;
    private String company_name;
    private String office_name;
    private String full_name;
    private String full_name_kana;
    private String phone_number;
    private int category;
    private String category_name;

    // private String user_name;

    // @Override
    // public void setEntity(ResultSet rs) {
    //     try {
    //         this.employee_id = rs.getInt("employee_id");
    //         this.code = rs.getInt("code");
    //         this.company_name = rs.getString("company_name");
    //         this.office_name = rs.getString("office_name");
    //         this.full_name = rs.getString("full_name");
    //         this.full_name_kana = rs.getString("full_name_kana");
    //         String phone = rs.getString("phone_number");
    //         this.phone_number = phone.isEmpty() ? null: phone;
    //         int category = rs.getInt("category");
    //         this.category = category;
    //         this.category_name = category == 0 ? null: Enums.employeeCategory.getDescriptionByNum(category);
    //     } catch (Exception e) {
    //         System.out.println(e);
    //     }
    // }
}
