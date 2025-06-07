package com.kyouseipro.neo.mapper.corporation;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.corporation.OfficeListEntity;

public class OfficeListEntityMapper {

    public static OfficeListEntity map(ResultSet rs) throws SQLException {
        OfficeListEntity entity = new OfficeListEntity();
        entity.setOffice_id(rs.getInt("office_id"));
        entity.setCompany_id(rs.getInt("company_id"));
        // entity.setCategory(rs.getInt("category"));
        entity.setName(rs.getString("name"));
        entity.setName_kana(rs.getString("name_kana"));
        entity.setTel_number(rs.getString("tel_number"));
        entity.setEmail(rs.getString("email"));
        return entity;
    }
}
