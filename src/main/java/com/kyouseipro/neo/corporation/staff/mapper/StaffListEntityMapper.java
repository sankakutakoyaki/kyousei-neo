package com.kyouseipro.neo.corporation.staff.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.corporation.staff.entity.StaffListEntity;

public class StaffListEntityMapper {
    public static StaffListEntity map(ResultSet rs) throws SQLException {
        StaffListEntity entity = new StaffListEntity();
        entity.setStaffId(rs.getInt("staff_id"));
        entity.setCompanyId(rs.getInt("company_id"));
        entity.setOfficeId(rs.getInt("office_id"));
        entity.setCompanyName(rs.getString("company_name"));
        entity.setOfficeName(rs.getString("office_name"));
        entity.setName(rs.getString("name"));
        entity.setNameKana(rs.getString("name_kana"));
        return entity;
    }
}
