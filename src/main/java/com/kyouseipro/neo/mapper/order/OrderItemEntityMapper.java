package com.kyouseipro.neo.mapper.order;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.order.OrderItemEntity;

public class OrderItemEntityMapper {    
    public static OrderItemEntity map(ResultSet rs) throws SQLException {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setOrder_item_id(rs.getInt("order_item_id"));
        entity.setOrder_id(rs.getInt("order_id"));
        entity.setCompany_id(rs.getInt("company_id"));
        entity.setCompany_name(rs.getString("company_name"));
        entity.setOffice_id(rs.getInt("office_id"));
        entity.setOffice_name(rs.getString("office_name"));
        entity.setDelivery_address(rs.getString("delivery_address"));
        entity.setArrival_date(rs.getDate("arrival_date").toLocalDate());
        entity.setInspector_id(rs.getInt("inspector_id"));
        entity.setInspector_name(rs.getString("inspector_name"));
        entity.setShipping_company_id(rs.getInt("shipping_company_id"));
        entity.setShipping_company_name(rs.getString("shipping_company_name"));
        entity.setDocument_number(rs.getString("document_number"));
        entity.setItem_maker(rs.getString("item_maker"));
        entity.setItem_name(rs.getString("item_name"));
        entity.setItem_model(rs.getString("item_model"));
        entity.setItem_quantity(rs.getInt("item_quantity"));
        entity.setItem_payment(rs.getInt("item_payment"));
        entity.setBuyer_id(rs.getInt("buyer_id"));
        entity.setBuyer_company_name(rs.getString("buyer_company_name"));
        entity.setBuyer_name(rs.getString("buyer_name"));
        entity.setRemarks(rs.getString("remarks"));
        int classification = rs.getInt("classification");
        entity.setClassification(classification);
        entity.setClassification_name(Enums.ItemClass.getDescriptionByNum(classification));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}