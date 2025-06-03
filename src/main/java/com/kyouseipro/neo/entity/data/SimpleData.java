package com.kyouseipro.neo.entity.data;

import lombok.Data;

@Data
public class SimpleData {
        
    private int number;
    private String text;

    // @Override
    // public void setEntity(ResultSet rs) {
    //     try{
    //         this.number = rs.getInt("number");
    //         this.text = rs.getString("text");
    //     } catch(Exception e) {
    //         System.out.println(e);
    //     }
    // }
}
