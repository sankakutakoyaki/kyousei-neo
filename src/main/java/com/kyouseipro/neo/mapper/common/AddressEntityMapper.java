package com.kyouseipro.neo.mapper.common;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.common.AddressEntity;

public class AddressEntityMapper {
    public static AddressEntity map(ResultSet rs) throws SQLException {
        AddressEntity entity = new AddressEntity();
        entity.setAddress_id(rs.getInt("address_id"));
        entity.setAddress_code(rs.getInt("address_code"));
        entity.setPrefecture_code(rs.getInt("prefecture_code"));
        entity.setCity_code(rs.getInt("city_code"));
        entity.setTown_code(rs.getInt("town_code"));
        entity.setPostal_code(rs.getString("postal_code"));
        entity.setPrefecture(rs.getString("prefecture"));
        entity.setPrefecture_kana(rs.getString("prefecture_kana"));
        entity.setCity(rs.getString("city"));
        entity.setCity_kana(rs.getString("city_kana"));
        entity.setTown(rs.getString("town"));
        entity.setTown_kana(rs.getString("town_kana"));
        return entity;
    }
}
