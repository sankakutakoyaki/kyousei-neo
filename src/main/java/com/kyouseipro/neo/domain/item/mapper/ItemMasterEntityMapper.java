package com.kyouseipro.neo.domain.item.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.domain.item.entity.ItemMasterEntity;

public class ItemMasterEntityMapper {

    public static ItemMasterEntity map(ResultSet rs) throws SQLException {

        ItemMasterEntity entity = new ItemMasterEntity();

        entity.setItemMasterId(rs.getLong("item_master_id"));
        entity.setJanCode(rs.getString("jan_code"));
        entity.setItemMaker(rs.getString("item_maker"));
        entity.setItemName(rs.getString("item_name"));
        entity.setItemModel(rs.getString("item_model"));
        entity.setState(rs.getInt("state"));
        entity.setVersion(rs.getInt("version"));

        return entity;
    }
}