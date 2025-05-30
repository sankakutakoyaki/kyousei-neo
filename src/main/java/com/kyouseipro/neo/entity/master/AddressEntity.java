package com.kyouseipro.neo.entity.master;

import java.sql.ResultSet;

import com.kyouseipro.neo.interfaceis.Entity;

import lombok.Data;

@Data
public class AddressEntity implements Entity {

    private int address_id;
    private int address_code;
    private int prefecture_code;
    private int city_code;
    private int town_code;
    private String postal_code;
    private String prefecture;
    private String prefecture_kana;
    private String city;
    private String city_kana;
    private String town;
    private String town_kana;

    @Override
    public void setEntity(ResultSet rs) {
        try{
            this.address_id = rs.getInt("address_id");
            this.address_code = rs.getInt("address_code");
            this.prefecture_code = rs.getInt("prefecture_code");
            this.city_code = rs.getInt("city_code");
            this.town_code = rs.getInt("town_code");
            this.postal_code = rs.getString("postal_code");
            this.prefecture = rs.getString("prefecture");
            this.prefecture_kana = rs.getString("prefecture_kana");
            this.city = rs.getString("city");
            this.city_kana = rs.getString("city_kana");
            this.town = rs.getString("town");
            this.town_kana = rs.getString("town_kana");
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}