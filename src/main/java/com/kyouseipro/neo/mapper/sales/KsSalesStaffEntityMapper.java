package com.kyouseipro.neo.mapper.sales;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.sales.KsSalesEntity;

public class KsSalesStaffEntityMapper {
    public static KsSalesEntity map(ResultSet rs) throws SQLException {
        KsSalesEntity entity = new KsSalesEntity();
        entity.setStore_name(rs.getString("store_name"));
        entity.setAmount(rs.getInt("amount"));
        entity.setStaff_company(rs.getString("staff_company"));
        entity.setStaff_name_1(rs.getString("staff_name_1"));
        return entity;
    }
}