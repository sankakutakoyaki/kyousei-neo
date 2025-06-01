package com.kyouseipro.neo.entity.corporation;

import java.sql.ResultSet;

import com.kyouseipro.neo.interfaceis.Entity;

import lombok.Data;

@Data
public class CompanyEntity implements Entity {
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

    public static CompanyEntity from(ResultSet rs) {
        CompanyEntity entity = new CompanyEntity();
        entity.setEntity(rs);
        return entity;
    }
}