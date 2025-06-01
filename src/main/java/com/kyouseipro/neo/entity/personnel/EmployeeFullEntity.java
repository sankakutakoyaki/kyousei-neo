package com.kyouseipro.neo.entity.personnel;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;

import com.kyouseipro.neo.interfaceis.Entity;

import lombok.Data;

@Data
public class EmployeeFullEntity implements Entity {
    private int employee_id;
    private int working_conditions_id;
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
    private int payment_method;
    private int pay_type;
    private int base_salary;
    private int trans_cost;
    private LocalTime basic_start_time;
    private LocalTime basic_end_time;
    private LocalDate date_of_hire;
    private int version;
    private int state;

    private String user_name;

    @Override
    public void setEntity(ResultSet rs) {
        try {
            this.employee_id = rs.getInt("employee_id");
            this.working_conditions_id = rs.getInt("working_conditions_id");
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
            this.payment_method = rs.getInt("payment_method");
            this.pay_type = rs.getInt("pay_type");
            this.base_salary = rs.getInt("base_salary");
            this.trans_cost = rs.getInt("trans_cost");
            this.basic_start_time = rs.getTime("basic_start_time").toLocalTime();
            this.basic_end_time = rs.getTime("basic_end_time").toLocalTime();
            this.date_of_hire = rs.getDate("date_of_hire").toLocalDate();
            this.version = rs.getInt("version");
            this.state = rs.getInt("state");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
