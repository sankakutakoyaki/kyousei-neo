package com.kyouseipro.neo.entity.corporation;

import java.sql.ResultSet;

import com.kyouseipro.neo.interfaceis.IEntity;

import lombok.Data;

@Data
public class CompanyListEntity implements IEntity {
    private int company_id;
    private int category;
    private String name;
    private String name_kana;
    private String tel_number;
    private String email;    
    
    @Override
    public void setEntity(ResultSet rs) {
        try {
            this.company_id = rs.getInt("company_id");
            this.category = rs.getInt("category");
            this.name = rs.getString("name");
            this.name_kana = rs.getString("name_kana");
            this.tel_number = rs.getString("tel_number");
            this.email = rs.getString("email");
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public static String selectString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT company_id, category, name, name_kana, tel_number, email FROM companies");
        return sb.toString();
    }
}

