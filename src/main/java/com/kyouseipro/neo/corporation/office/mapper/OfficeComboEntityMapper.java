package com.kyouseipro.neo.corporation.office.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.corporation.office.entity.OfficeComboEntity;

public class OfficeComboEntityMapper {
    public static OfficeComboEntity map(ResultSet rs) throws SQLException {
        OfficeComboEntity entity = new OfficeComboEntity();
        entity.setOfficeId(rs.getInt("office_id"));
        entity.setCompanyId(rs.getInt("company_id"));
        entity.setOfficeName(rs.getString("office_name"));
        return entity;
    }
}
