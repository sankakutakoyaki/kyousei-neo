package com.kyouseipro.neo.recycle.regist.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Utilities;
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
        entity.setUseDate(Utilities.toLocalDate(rs, "use_date"));
        entity.setDeliveryDate(Utilities.toLocalDate(rs, "delivery_date"));
        entity.setShippingDate(Utilities.toLocalDate(rs, "shipping_date"));
        entity.setLossDate(Utilities.toLocalDate(rs, "loss_date"));
        entity.setCompanyId(rs.getInt("company_id"));
        entity.setCompanyName(rs.getString("company_name"));
        entity.setOfficeId(rs.getInt("office_id"));
        entity.setOfficeName(rs.getString("office_name"));
        entity.setRecyclingFee(rs.getInt("recycling_fee"));
        entity.setDisposalSiteId(rs.getInt("disposal_site_id"));
        entity.setDisposalSiteName(rs.getString("disposal_site_name"));
        entity.setSlipNumber(rs.getInt("slip_number"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        entity.setRegistDate(Utilities.toLocalDate(rs, "regist_date"));
        entity.setUpdateDate(Utilities.toLocalDate(rs, "update_date"));
        return entity;
    }
}