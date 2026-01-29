package com.kyouseipro.neo.mapper.common;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.common.AddressEntity;

public class AddressEntityMapper {
    public static AddressEntity map(ResultSet rs) throws SQLException {
        AddressEntity entity = new AddressEntity();
        entity.setAddressId(rs.getInt("address_id"));
        entity.setAddressCode(rs.getInt("address_code"));
        entity.setPrefectureCode(rs.getInt("prefecture_code"));
        entity.setCityCode(rs.getInt("city_code"));
        entity.setTownCode(rs.getInt("town_code"));
        entity.setPostalCode(rs.getString("postal_code"));
        entity.setPrefecture(rs.getString("prefecture"));
        entity.setPrefectureKana(rs.getString("prefecture_kana"));
        entity.setCity(rs.getString("city"));
        entity.setCityKana(rs.getString("city_kana"));
        entity.setTown(rs.getString("town"));
        entity.setTownKana(rs.getString("town_kana"));
        return entity;
    }
}
