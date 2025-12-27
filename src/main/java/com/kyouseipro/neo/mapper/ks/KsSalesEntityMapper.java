package com.kyouseipro.neo.mapper.ks;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.ks.KsSalesEntity;

public class KsSalesEntityMapper {
    public static KsSalesEntity map(ResultSet rs) throws SQLException {
        KsSalesEntity entity = new KsSalesEntity();
        entity.setKs_sales_id(rs.getInt("ks_sales_id"));
        entity.setTarget_year_month(rs.getString("target_year_month"));
        entity.setPartner_store_code(rs.getInt("partner_store_code"));
        entity.setPartner_store_name(rs.getString("partner_store_name"));
        entity.setCorporation_code(rs.getInt("corporation_code"));
        entity.setCorporation_name(rs.getString("corporation_name"));
        entity.setStore_code(rs.getInt("store_code"));
        entity.setStore_name(rs.getString("store_name"));
        entity.setSlip_date(rs.getDate("slip_date").toLocalDate());
        entity.setPurchase_slip_number(rs.getInt("purchase_slip_number"));
        entity.setPurchase_slip_type(rs.getString("purchase_slip_type"));
        entity.setJan_code(rs.getString("jan_code"));
        entity.setModel_number(rs.getString("model_number"));
        entity.setQuantity(rs.getInt("quantity"));
        entity.setAmount(rs.getInt("amount"));
        entity.setDelivery_payment_mgmt_number(rs.getInt("delivery_payment_mgmt_number"));
        entity.setSales_slip_number(rs.getInt("sales_slip_number"));
        entity.setSales_store_code(rs.getInt("sales_store_code"));
        entity.setSales_store_name(rs.getString("sales_store_name"));
        entity.setStaff_company(rs.getString("staff_company"));
        entity.setStaff_code_1(rs.getInt("staff_code_1"));
        entity.setStaff_name_1(rs.getString("staff_name_1"));
        entity.setStaff_code_2(rs.getInt("staff_code_2"));
        entity.setStaff_name_2(rs.getString("staff_name_2"));
        entity.setDelivery_date(rs.getDate("delivery_date").toLocalDate());
        return entity;
    }
}
