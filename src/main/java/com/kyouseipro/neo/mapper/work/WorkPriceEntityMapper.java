package com.kyouseipro.neo.mapper.work;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.work.WorkPriceEntity;

public class WorkPriceEntityMapper {
    public static WorkPriceEntity map(ResultSet rs) throws SQLException {
        WorkPriceEntity entity = new WorkPriceEntity();
        entity.setWorkPriceId(rs.getInt("work_price_id"));
        entity.setWorkItemId(rs.getInt("work_item_id"));
        entity.setWorkItemName(rs.getString("work_item_name"));
        entity.setFullCode(rs.getInt("full_code"));
        entity.setCompanyId(rs.getInt("company_id"));
        entity.setCompanyName(rs.getString("company_name"));
        entity.setCategoryId(rs.getInt("category_id"));
        entity.setCategoryName(rs.getString("category_name"));
        entity.setPrice(rs.getInt("price"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
