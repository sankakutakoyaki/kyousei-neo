package com.kyouseipro.neo.mapper.corporation;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.corporation.CompanyEntity;

public class CompanyEntityMapper {
    public static CompanyEntity map(ResultSet rs) throws SQLException {
        CompanyEntity entity = new CompanyEntity();
        entity.setCompanyId(rs.getInt("company_id"));
        entity.setCategory(rs.getInt("category"));
        entity.setName(rs.getString("name"));
        entity.setNameKana(rs.getString("name_kana"));
        entity.setTelNumber(rs.getString("tel_number"));
        entity.setFaxNumber(rs.getString("fax_number"));
        entity.setPostalCode(rs.getString("postal_code"));
        entity.setFullAddress(rs.getString("full_address"));
        entity.setEmail(rs.getString("email"));
        entity.setWebAddress(rs.getString("web_address"));
        entity.setIsOriginalPrice(rs.getInt("is_original_price"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
