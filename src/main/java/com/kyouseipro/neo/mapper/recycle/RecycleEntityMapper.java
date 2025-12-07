package com.kyouseipro.neo.mapper.recycle;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.recycle.RecycleEntity;

public class RecycleEntityMapper {
    public static RecycleEntity map(ResultSet rs) throws SQLException {
        RecycleEntity entity = new RecycleEntity();
        entity.setRecycle_id(rs.getInt("recycle_id"));
        entity.setNumber(rs.getString("number"));
        entity.setMaker_id(rs.getInt("maker_id"));
        entity.setMaker_name(rs.getString("maker_name"));
        entity.setItem_id(rs.getInt("item_id"));
        entity.setItem_name(rs.getString("item_name"));
        entity.setUse_date(rs.getDate("use_date").toLocalDate());
        entity.setDelivery_date(rs.getDate("delivery_date").toLocalDate());
        entity.setShipping_date(rs.getDate("shipping_date").toLocalDate());
        entity.setLoss_date(rs.getDate("loss_date").toLocalDate());
        entity.setCompany_id(rs.getInt("company_id"));
        entity.setCompany_name(rs.getString("company_name"));
        entity.setOffice_id(rs.getInt("office_id"));
        entity.setOffice_name(rs.getString("office_name"));
        entity.setRecycle_fee(rs.getInt("recycle_fee"));
        entity.setDisposal_site_id(rs.getInt("disposal_site_id"));
        entity.setDisposal_site_name(rs.getString("disposal_site_name"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}