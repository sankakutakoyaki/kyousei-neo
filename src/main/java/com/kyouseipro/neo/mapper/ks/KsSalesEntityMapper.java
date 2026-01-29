package com.kyouseipro.neo.mapper.ks;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.ks.KsSalesEntity;

public class KsSalesEntityMapper {
    public static KsSalesEntity map(ResultSet rs) throws SQLException {
        KsSalesEntity entity = new KsSalesEntity();
        entity.setKsSalesId(rs.getInt("ks_sales_id"));
        entity.setTargetYearMonth(rs.getString("target_year_month"));
        entity.setPartnerStoreCode(rs.getInt("partner_store_code"));
        entity.setPartnerStoreName(rs.getString("partner_store_name"));
        entity.setCorporationCode(rs.getInt("corporation_code"));
        entity.setCorporationName(rs.getString("corporation_name"));
        entity.setStoreCode(rs.getInt("store_code"));
        entity.setStoreName(rs.getString("store_name"));
        entity.setSlipDate(rs.getDate("slip_date").toLocalDate());
        entity.setPurchaseSlipNumber(rs.getInt("purchase_slip_number"));
        entity.setPurchaseSlipType(rs.getString("purchase_slip_type"));
        entity.setJanCode(rs.getString("jan_code"));
        entity.setModelNumber(rs.getString("model_number"));
        entity.setQuantity(rs.getInt("quantity"));
        entity.setAmount(rs.getInt("amount"));
        entity.setDeliveryPaymentMgmtNumber(rs.getInt("delivery_payment_mgmt_number"));
        entity.setSalesSlipNumber(rs.getInt("sales_slip_number"));
        entity.setSalesStoreCode(rs.getInt("sales_store_code"));
        entity.setSalesStoreName(rs.getString("sales_store_name"));
        entity.setStaffCompany(rs.getString("staff_company"));
        entity.setStaffCode1(rs.getInt("staff_code_1"));
        entity.setStaffName1(rs.getString("staff_name_1"));
        entity.setStaffCode2(rs.getInt("staff_code_2"));
        entity.setStaffName2(rs.getString("staff_name_2"));
        entity.setDeliveryDate(rs.getDate("delivery_date").toLocalDate());
        return entity;
    }
}
