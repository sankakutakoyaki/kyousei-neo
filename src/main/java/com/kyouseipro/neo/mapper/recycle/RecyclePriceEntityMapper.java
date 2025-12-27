package com.kyouseipro.neo.mapper.recycle;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.recycle.RecyclePriceEntity;

public class RecyclePriceEntityMapper {
    public static RecyclePriceEntity map(ResultSet rs) throws SQLException {
        RecyclePriceEntity entity = new RecyclePriceEntity();
        entity.setRecycle_price_id(rs.getInt("recycle_price_id"));
        entity.setRecycle_maker_id(rs.getInt("recycle_maker_id"));
        entity.setRecycle_item_id(rs.getInt("recycle_item_id"));
        entity.setPrice(rs.getInt("price"));
        entity.setTax_price(rs.getInt("tax_price"));
        return entity;
    }
}
