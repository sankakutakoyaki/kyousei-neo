package com.kyouseipro.neo.ks.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.ks.entity.KsSalesEntity;

public class KsSalesStaffEntityMapper {
    public static KsSalesEntity map(ResultSet rs) throws SQLException {
        KsSalesEntity entity = new KsSalesEntity();
        entity.setStoreName(rs.getString("store_name"));
        entity.setAmount(rs.getInt("amount"));
        entity.setStaffCompany(rs.getString("staff_company"));
        entity.setStaffName1(rs.getString("staff_name_1"));
        return entity;
    }
}