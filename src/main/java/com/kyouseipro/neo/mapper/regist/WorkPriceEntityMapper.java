package com.kyouseipro.neo.mapper.regist;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.regist.WorkPriceEntity;

public class WorkPriceEntityMapper {
    public static WorkPriceEntity map(ResultSet rs) throws SQLException {
        WorkPriceEntity entity = new WorkPriceEntity();
        entity.setWork_price_id(rs.getInt("work_price_id"));
        entity.setWork_item_id(rs.getInt("work_item_id"));
        entity.setWork_item_name(rs.getString("work_item_name"));
        entity.setCompany_id(rs.getInt("company_id"));
        entity.setCompany_name(rs.getString("company_name"));
        entity.setPrice(rs.getInt("price"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
