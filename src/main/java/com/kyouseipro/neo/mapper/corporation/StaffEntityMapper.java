package com.kyouseipro.neo.mapper.corporation;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.corporation.StaffEntity;

public class StaffEntityMapper {
    public static StaffEntity map(ResultSet rs) throws SQLException {
        StaffEntity entity = new StaffEntity();
        entity.setStaff_id(rs.getInt("staff_id"));
        entity.setCompany_id(rs.getInt("company_id"));
        entity.setOffice_id(rs.getInt("office_id"));
        entity.setCompany_name(rs.getString("company_name"));
        entity.setOffice_name(rs.getString("office_name"));
        entity.setName(rs.getString("name"));
        entity.setName_kana(rs.getString("name_kana"));
        entity.setPhone_number(rs.getString("phone_number"));
        entity.setEmail(rs.getString("email"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
