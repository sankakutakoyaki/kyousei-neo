package com.kyouseipro.neo.recycle.price.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.recycle.price.entity.RecyclePriceEntity;

public class RecyclePriceEntityMapper {
    public static RecyclePriceEntity map(ResultSet rs) throws SQLException {
        RecyclePriceEntity entity = new RecyclePriceEntity();
        entity.setRecyclePriceId(rs.getInt("recycle_price_id"));
        entity.setRecycleMakerId(rs.getInt("recycle_maker_id"));
        entity.setRecycleItemId(rs.getInt("recycle_item_id"));
        entity.setPrice(rs.getInt("price"));
        entity.setTaxPrice(rs.getInt("tax_price"));
        return entity;
    }
}
