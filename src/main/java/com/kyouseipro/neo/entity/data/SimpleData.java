package com.kyouseipro.neo.entity.data;

import java.sql.ResultSet;

import com.kyouseipro.neo.interfaceis.Entity;

import lombok.Data;

@Data
public class SimpleData implements Entity {
        
    private int number;
    private String text;

    @Override
    public void setEntity(ResultSet rs) {
        try{
            this.number = rs.getInt("number");
            this.text = rs.getString("text");
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
