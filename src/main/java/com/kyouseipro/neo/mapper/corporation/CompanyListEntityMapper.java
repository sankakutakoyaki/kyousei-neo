package com.kyouseipro.neo.mapper.corporation;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.corporation.CompanyListEntity;

public class CompanyListEntityMapper {
    public static CompanyListEntity map(ResultSet rs) throws SQLException {
        CompanyListEntity entity = new CompanyListEntity();
        entity.setCompany_id(rs.getInt("company_id"));
        entity.setCategory(rs.getInt("category"));
        entity.setName(rs.getString("name"));
        entity.setName_kana(rs.getString("name_kana"));
        entity.setTel_number(rs.getString("tel_number"));
        entity.setEmail(rs.getString("email"));
        return entity;
    }
}
