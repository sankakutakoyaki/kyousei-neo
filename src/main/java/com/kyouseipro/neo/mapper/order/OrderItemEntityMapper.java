package com.kyouseipro.neo.mapper.order;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.order.OrderItemEntity;

public class OrderItemEntityMapper {    
    public static OrderItemEntity map(ResultSet rs) throws SQLException {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setOrderItemId(rs.getInt("order_item_id"));
        entity.setOrderId(rs.getInt("order_id"));
        entity.setCompanyId(rs.getInt("company_id"));
        entity.setCompanyName(rs.getString("company_name"));
        entity.setOfficeId(rs.getInt("office_id"));
        entity.setOfficeName(rs.getString("office_name"));
        entity.setDeliveryAddress(rs.getString("delivery_address"));
        entity.setArrivalDate(rs.getDate("arrival_date").toLocalDate());
        entity.setInspectorId(rs.getInt("inspector_id"));
        entity.setInspectorName(rs.getString("inspector_name"));
        entity.setShippingCompanyId(rs.getInt("shipping_company_id"));
        entity.setShippingCompanyName(rs.getString("shipping_company_name"));
        entity.setDocumentNumber(rs.getString("document_number"));
        entity.setItemMaker(rs.getString("item_maker"));
        entity.setItemName(rs.getString("item_name"));
        entity.setItemModel(rs.getString("item_model"));
        entity.setItemQuantity(rs.getInt("item_quantity"));
        entity.setItemPayment(rs.getInt("item_payment"));
        entity.setBuyerId(rs.getInt("buyer_id"));
        entity.setBuyerCompanyName(rs.getString("buyer_company_name"));
        entity.setBuyerName(rs.getString("buyer_name"));
        entity.setRemarks(rs.getString("remarks"));
        int classification = rs.getInt("classification");
        entity.setClassification(classification);
        entity.setClassificationName(Enums.ItemClass.getDescriptionByNum(classification));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}