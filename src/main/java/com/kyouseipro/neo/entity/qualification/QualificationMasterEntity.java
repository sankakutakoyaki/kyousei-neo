package com.kyouseipro.neo.entity.qualification;

import lombok.Data;

@Data
public class QualificationMasterEntity {

    private int qualification_master_id;
    private int code;
    private String name;
    private String category_name;
    private String organization;
    private int validity_years;
    private int state;

    // @Override
    // public void setEntity(ResultSet rs) {
    //     try{
    //         this.qualification_master_id = rs.getInt("qualification_master_id");
    //         this.code = rs.getInt("code");
    //         this.name = rs.getString("name");
    //         this.category_name = rs.getString("category_name");
    //         this.organization = rs.getString("organization");
    //         this.validity_years = rs.getInt("validity_years");
    //         this.state = rs.getInt("state");
    //     } catch(Exception e) {
    //         System.out.println(e);
    //     }
    // }
}
