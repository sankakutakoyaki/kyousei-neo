package com.kyouseipro.neo.entity.corporation;

import java.sql.ResultSet;

import com.kyouseipro.neo.interfaceis.IEntity;

import lombok.Data;

@Data
public class OfficeComboEntity implements IEntity {
    
    private int office_id;
    private int company_id;
    private String office_name;

    @Override
    public void setEntity(ResultSet rs) {
        try {
            this.office_id = rs.getInt("office_id");
            this.company_id = rs.getInt("company_id");
            this.office_name = rs.getString("office_name");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
