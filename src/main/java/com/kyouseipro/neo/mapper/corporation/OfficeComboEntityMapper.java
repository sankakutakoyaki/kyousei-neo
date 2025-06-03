package com.kyouseipro.neo.mapper.corporation;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.corporation.OfficeComboEntity;

public class OfficeComboEntityMapper {
    public static OfficeComboEntity map(ResultSet rs) throws SQLException {
        OfficeComboEntity entity = new OfficeComboEntity();
        entity.setOffice_id(rs.getInt("office_id"));
        entity.setCompany_id(rs.getInt("company_id"));
        entity.setOffice_name(rs.getString("office_name"));
        return entity;
    }
}
