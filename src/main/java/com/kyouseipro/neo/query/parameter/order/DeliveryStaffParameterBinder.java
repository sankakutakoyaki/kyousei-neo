package com.kyouseipro.neo.query.parameter.order;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.order.DeliveryStaffEntity;

public class DeliveryStaffParameterBinder {
    public static int bindInsertDeliveryStaffParameters(PreparedStatement pstmt, DeliveryStaffEntity d, String editor, int index, boolean isNew) throws SQLException {
        if (isNew == false) {
            pstmt.setInt(index++, d.getOrder_id());
        }
        pstmt.setInt(index++, d.getEmployee_id());
        pstmt.setInt(index++, d.getVersion());
        pstmt.setInt(index++, d.getState());

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindUpdateDeliveryStaffParameters(PreparedStatement pstmt, DeliveryStaffEntity d, String editor, int index) throws SQLException {
        pstmt.setInt(index++, d.getOrder_id());
        pstmt.setInt(index++, d.getEmployee_id());
        pstmt.setInt(index++, d.getVersion());
        pstmt.setInt(index++, d.getState());

        pstmt.setInt(index++, d.getDelivery_staff_id()); // WHERE句
        pstmt.setString(index++, editor);          // ログ用
        return index;
    }

    public static int bindFindAllByOrderId(PreparedStatement ps, int id) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindDeleteDeliveryStaffParameters(PreparedStatement ps, int id, String editor, int index) throws SQLException {
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setString(index, editor);
        return index;
    }

    public static int setSaveDeliveryStaffListParameters(PreparedStatement pstmt, List<DeliveryStaffEntity> list, String editor, int index) throws SQLException {
        for (DeliveryStaffEntity deliveryStaffEntity : list) {
            // 削除の場合
            if (deliveryStaffEntity.getState() == Enums.state.DELETE.getCode()) {
                index = DeliveryStaffParameterBinder.bindDeleteDeliveryStaffParameters(pstmt, deliveryStaffEntity.getDelivery_staff_id(), editor, index);
                // index = index + 3;
            } else {
                // 更新か新規かで分岐
                if (deliveryStaffEntity.getDelivery_staff_id() > 0){
                    index = DeliveryStaffParameterBinder.bindUpdateDeliveryStaffParameters(pstmt, deliveryStaffEntity, editor, index);
                    // index = index + 6;
                } else {
                    index = DeliveryStaffParameterBinder.bindInsertDeliveryStaffParameters(pstmt, deliveryStaffEntity, editor, index, false);
                    // index = index + 5;
                }
            }
        }
        return index;
    }
}
