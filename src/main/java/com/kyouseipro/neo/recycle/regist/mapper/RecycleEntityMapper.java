package com.kyouseipro.neo.recycle.regist.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.recycle.regist.entity.RecycleEntity;

public class RecycleEntityMapper {
    public static RecycleEntity map(ResultSet rs) throws SQLException {
        RecycleEntity entity = new RecycleEntity();
        entity.setRecycleId(rs.getInt("recycle_id"));
        entity.setRecycleNumber(rs.getString("recycle_number"));
        entity.setMoldingNumber(rs.getString("molding_number"));
        entity.setMakerId(rs.getInt("maker_id"));
        entity.setMakerCode(rs.getInt("maker_code"));
        entity.setMakerName(rs.getString("maker_name"));
        entity.setItemId(rs.getInt("item_id"));
        entity.setItemCode(rs.getInt("item_code"));
        entity.setItemName(rs.getString("item_name"));
        entity.setUseDate(rs.getDate("use_date").toLocalDate());
        entity.setDeliveryDate(rs.getDate("delivery_date").toLocalDate());
        entity.setShippingDate(rs.getDate("shipping_date").toLocalDate());
        entity.setLossDate(rs.getDate("loss_date").toLocalDate());
        entity.setCompanyId(rs.getInt("company_id"));
        entity.setCompanyName(rs.getString("company_name"));
        entity.setOfficeId(rs.getInt("office_id"));
        entity.setOfficeName(rs.getString("office_name"));
        entity.setRecyclingFee(rs.getInt("recycling_fee"));
        entity.setDisposalSiteId(rs.getInt("disposal_site_id"));
        entity.setDisposalSiteName(rs.getString("disposal_site_name"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        entity.setRegistDate(rs.getDate("regist_date").toLocalDate());
        entity.setUpdateDate(rs.getDate("update_date").toLocalDate());
        return entity;
    }
}