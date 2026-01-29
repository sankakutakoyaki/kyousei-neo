package com.kyouseipro.neo.mapper.corporation;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.corporation.OfficeListEntity;

public class OfficeListEntityMapper {

    public static OfficeListEntity map(ResultSet rs) throws SQLException {
        OfficeListEntity entity = new OfficeListEntity();
        entity.setOfficeId(rs.getInt("office_id"));
        entity.setCompanyId(rs.getInt("company_id"));
        // entity.setCategory(rs.getInt("category"));
        entity.setName(rs.getString("name"));
        entity.setNameKana(rs.getString("name_kana"));
        entity.setTelNumber(rs.getString("tel_number"));
        entity.setEmail(rs.getString("email"));
        return entity;
    }
}
