package com.kyouseipro.neo.corporation.staff.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.corporation.staff.entity.StaffEntity;

public class StaffEntityMapper {
    public static StaffEntity map(ResultSet rs) throws SQLException {
        StaffEntity entity = new StaffEntity();
        entity.setStaffId(rs.getInt("staff_id"));
        entity.setCompanyId(rs.getInt("company_id"));
        entity.setOfficeId(rs.getInt("office_id"));
        entity.setCompanyName(rs.getString("company_name"));
        entity.setOfficeName(rs.getString("office_name"));
        entity.setName(rs.getString("name"));
        entity.setNameKana(rs.getString("name_kana"));
        entity.setPhoneNumber(rs.getString("phone_number"));
        entity.setEmail(rs.getString("email"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
