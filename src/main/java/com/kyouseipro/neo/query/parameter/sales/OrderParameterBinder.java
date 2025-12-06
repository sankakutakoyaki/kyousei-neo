package com.kyouseipro.neo.query.parameter.sales;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.sales.DeliveryStaffEntity;
import com.kyouseipro.neo.entity.sales.OrderEntity;
import com.kyouseipro.neo.entity.sales.OrderItemEntity;

public class OrderParameterBinder {
    public static void bindInsertOrderParameters(PreparedStatement pstmt, OrderEntity o, String editor) throws SQLException {
        int index = 1;
        pstmt.setString(index++, o.getRequest_number());
        // if (o.getOrder_date() != null) {
        //     pstmt.setDate(index++, java.sql.Date.valueOf(o.getOrder_date()));
        // } else {
        //     pstmt.setNull(index++, java.sql.Types.DATE);
        // }
        if (o.getStart_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getStart_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        if (o.getEnd_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getEnd_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        pstmt.setInt(index++, o.getPrime_constractor_id());
        pstmt.setInt(index++, o.getPrime_constractor_office_id());
        pstmt.setString(index++, o.getTitle());
        pstmt.setString(index++, o.getOrder_postal_code());
        pstmt.setString(index++, o.getOrder_full_address());
        pstmt.setString(index++, o.getContact_information());
        pstmt.setString(index++, o.getContact_information2());
        pstmt.setString(index++, o.getRemarks());

        pstmt.setInt(index++, o.getVersion());
        pstmt.setInt(index++, o.getState());

        pstmt.setString(index++, editor);

        for (OrderItemEntity orderItemEntity : o.getItem_list()) {
            OrderItemParameterBinder.bindInsertOrderItemParameters(pstmt, orderItemEntity, editor, index, true);
            index = index + 8;
        }
    }

    public static void bindUpdateOrderParameters(PreparedStatement pstmt, OrderEntity o, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, o.getOrder_id());
        pstmt.setString(index++, o.getRequest_number());
        // if (o.getOrder_date() != null) {
        //     pstmt.setDate(index++, java.sql.Date.valueOf(o.getOrder_date()));
        // } else {
        //     pstmt.setNull(index++, java.sql.Types.DATE);
        // }
        if (o.getStart_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getStart_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        if (o.getEnd_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getEnd_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        pstmt.setInt(index++, o.getPrime_constractor_id());
        pstmt.setInt(index++, o.getPrime_constractor_office_id());
        pstmt.setString(index++, o.getTitle());
        pstmt.setString(index++, o.getOrder_postal_code());
        pstmt.setString(index++, o.getOrder_full_address());
        pstmt.setString(index++, o.getContact_information());
        pstmt.setString(index++, o.getContact_information2());
        pstmt.setString(index++, o.getRemarks());

        pstmt.setInt(index++, o.getVersion());
        pstmt.setInt(index++, o.getState());

        pstmt.setInt(index++, o.getOrder_id()); // WHERE句
        pstmt.setString(index++, editor);          // ログ用

        // 商品リスト
        for (OrderItemEntity orderItemEntity : o.getItem_list()) {
            // 削除の場合
            if (orderItemEntity.getState() == Enums.state.DELETE.getCode()) {
                OrderItemParameterBinder.bindDeleteOrderItemParameters(pstmt, orderItemEntity.getOrder_item_id(), editor, index);
                index = index + 3;
            } else {
                // 更新か新規かで分岐
                if (orderItemEntity.getOrder_item_id() > 0){
                    OrderItemParameterBinder.bindUpdateOrderItemParameters(pstmt, orderItemEntity, editor, index);
                    index = index + 19;
                } else {
                    OrderItemParameterBinder.bindInsertOrderItemParameters(pstmt, orderItemEntity, editor, index, false);
                    index = index + 18;
                }
            }
        }

        // 配送担当リスト
        for (DeliveryStaffEntity deliveryStaffEntity : o.getStaff_list()) {
            // 削除の場合
            if (deliveryStaffEntity.getState() == Enums.state.DELETE.getCode()) {
                DeliveryStaffParameterBinder.bindDeleteDeliveryStaffParameters(pstmt, deliveryStaffEntity.getDelivery_staff_id(), editor, index);
                index = index + 3;
            } else {
                // 更新か新規かで分岐
                if (deliveryStaffEntity.getDelivery_staff_id() > 0){
                    DeliveryStaffParameterBinder.bindUpdateDeliveryStaffParameters(pstmt, deliveryStaffEntity, editor, index);
                    index = index + 6;
                } else {
                    DeliveryStaffParameterBinder.bindInsertDeliveryStaffParameters(pstmt, deliveryStaffEntity, editor, index, false);
                    index = index + 5;
                }
            }
        }
    }

    public static void bindFindById(PreparedStatement ps, Integer orderId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, orderId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
    }

    // public static void bindFindByAccount(PreparedStatement ps, String account) throws SQLException {
    //     ps.setString(1, account);
    //     ps.setInt(2, Enums.state.DELETE.getCode());
    // }

    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
    }

    public static void bindDeleteForIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index, editor); // 4. log
    }

    public static void bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id); // company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // AND NOT (state = ?)
    }
}
