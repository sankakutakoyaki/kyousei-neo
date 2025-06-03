package com.kyouseipro.neo.mapper.corporation;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.corporation.CompanyEntity;

public class CompanyEntityMapper {
    public static CompanyEntity map(ResultSet rs) throws SQLException {
        CompanyEntity entity = new CompanyEntity();
        entity.setCompany_id(rs.getInt("company_id"));
        entity.setCategory(rs.getInt("category"));
        entity.setName(rs.getString("name"));
        entity.setName_kana(rs.getString("name_kana"));
        entity.setTel_number(rs.getString("tel_number"));
        entity.setFax_number(rs.getString("fax_number"));
        entity.setPostal_code(rs.getString("postal_code"));
        entity.setFull_address(rs.getString("full_address"));
        entity.setEmail(rs.getString("email"));
        entity.setWeb_address(rs.getString("web_address"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
