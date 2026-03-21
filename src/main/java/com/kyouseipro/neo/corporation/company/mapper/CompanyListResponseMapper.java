package com.kyouseipro.neo.corporation.company.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.corporation.company.dto.CompanyListResponse;

public class CompanyListResponseMapper {
    public static CompanyListResponse map(ResultSet rs) throws SQLException {
        CompanyListResponse entity = new CompanyListResponse();
        entity.setCompanyId(rs.getLong("company_id"));
        entity.setCategory(rs.getInt("category"));
        entity.setName(rs.getString("name"));
        entity.setNameKana(rs.getString("name_kana"));
        entity.setTelNumber(rs.getString("tel_number"));
        entity.setEmail(rs.getString("email"));
        return entity;
    }
}
