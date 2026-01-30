package com.kyouseipro.neo.corporation.office.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.corporation.office.entity.OfficeEntity;

public class OfficeEntityMapper {
    
    public static OfficeEntity map(ResultSet rs) throws SQLException {
        OfficeEntity entity = new OfficeEntity();
        entity.setOfficeId(rs.getInt("office_id"));
        entity.setCompanyId(rs.getInt("company_id"));
        entity.setCompanyName(rs.getString("company_name"));
        entity.setName(rs.getString("name"));
        entity.setNameKana(rs.getString("name_kana"));
        entity.setTelNumber(rs.getString("tel_number"));
        entity.setFaxNumber(rs.getString("fax_number"));
        entity.setPostalCode(rs.getString("postal_code"));
        entity.setFullAddress(rs.getString("full_address"));
        entity.setEmail(rs.getString("email"));
        entity.setWebAddress(rs.getString("web_address"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
