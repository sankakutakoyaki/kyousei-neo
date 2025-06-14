package com.kyouseipro.neo.mapper.corporation;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.corporation.StaffListEntity;

public class StaffListEntityMapper {
    public static StaffListEntity map(ResultSet rs) throws SQLException {
        StaffListEntity entity = new StaffListEntity();
        entity.setStaff_id(rs.getInt("staff_id"));
        entity.setCompany_id(rs.getInt("company_id"));
        entity.setOffice_id(rs.getInt("office_id"));
        entity.setCompany_name(rs.getString("company_name"));
        entity.setOffice_name(rs.getString("office_name"));
        entity.setName(rs.getString("name"));
        entity.setName_kana(rs.getString("name_kana"));
        return entity;
    }
}
