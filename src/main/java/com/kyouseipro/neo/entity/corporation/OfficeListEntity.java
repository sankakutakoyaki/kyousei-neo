package com.kyouseipro.neo.entity.corporation;

import java.sql.ResultSet;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.interfaceis.IEntity;

import lombok.Data;

@Data
public class OfficeListEntity implements IEntity {

    private int office_id;
    private int company_id;
    private int category;
    private String name;
    private String name_kana;
    private String tel_number;
    private String email;    
    
    @Override
    public void setEntity(ResultSet rs) {
        try {
            this.office_id = rs.getInt("office_id");
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
        sb.append("SELECT o.*, c.category FROM offices o");
        sb.append(" INNER JOIN companies c ON c.company_id = o.company_id AND NOT (c.state = " + Enums.state.DELETE.getNum() + ")");
        return sb.toString();
    }
}