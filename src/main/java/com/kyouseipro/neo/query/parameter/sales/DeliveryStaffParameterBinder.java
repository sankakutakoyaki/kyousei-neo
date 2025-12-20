package com.kyouseipro.neo.query.parameter.sales;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.sales.DeliveryStaffEntity;
import com.kyouseipro.neo.entity.sales.OrderItemEntity;

public class DeliveryStaffParameterBinder {
    public static void bindInsertDeliveryStaffParameters(PreparedStatement pstmt, DeliveryStaffEntity d, String editor, int index, boolean isNew) throws SQLException {
        if (isNew == false) {
            pstmt.setInt(index++, d.getOrder_id());
        }
        pstmt.setInt(index++, d.getEmployee_id());
        pstmt.setInt(index++, d.getVersion());
        pstmt.setInt(index++, d.getState());

        pstmt.setString(index++, editor);
    }

    public static void bindUpdateDeliveryStaffParameters(PreparedStatement pstmt, DeliveryStaffEntity d, String editor, int index) throws SQLException {
        pstmt.setInt(index++, d.getOrder_id());
        pstmt.setInt(index++, d.getEmployee_id());
        pstmt.setInt(index++, d.getVersion());
        pstmt.setInt(index++, d.getState());

        pstmt.setInt(index++, d.getDelivery_staff_id()); // WHERE句
        pstmt.setString(index++, editor);          // ログ用
    }

    // public static void bindFindById(PreparedStatement ps, Integer orderId) throws SQLException {
    //     int index = 1;
    //     ps.setInt(index++, Enums.state.DELETE.getCode());
    //     ps.setInt(index++, Enums.state.DELETE.getCode());
    //     ps.setInt(index++, orderId);
    //     ps.setInt(index++, Enums.state.DELETE.getCode());
    // }

    // public static void bindFindByAccount(PreparedStatement ps, String account) throws SQLException {
    //     ps.setString(1, account);
    //     ps.setInt(2, Enums.state.DELETE.getCode());
    // }

    // public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
    //     int index = 1;
    //     ps.setInt(index++, Enums.state.DELETE.getCode());
    // }

    public static void bindFindAllByOrderId(PreparedStatement ps, int id) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setInt(index++, Enums.state.DELETE.getCode());
    }

    public static void bindDeleteDeliveryStaffParameters(PreparedStatement ps, int id, String editor, int index) throws SQLException {
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setString(index, editor);
    }

    // public static void bindDeleteForIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
    //     int index = 1;
    //     ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
    //     for (Integer id : ids) {
    //         ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
    //     }
    //     ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
    //     ps.setString(index, editor); // 4. log
    // }

    // public static void bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
    //     int index = 1;
    //     ps.setInt(index++, Enums.state.DELETE.getCode());
    //     ps.setInt(index++, Enums.state.DELETE.getCode());
    //     for (Integer id : ids) {
    //         ps.setInt(index++, id); // company_id IN (?, ?, ?)
    //     }
    //     ps.setInt(index++, Enums.state.DELETE.getCode()); // AND NOT (state = ?)
    // }

    public static int setSaveDeliveryStaffListParameters(PreparedStatement pstmt, List<DeliveryStaffEntity> list, String editor, int index) throws SQLException {
        for (DeliveryStaffEntity deliveryStaffEntity : list) {
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
        return index;
    }
}
