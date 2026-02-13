package com.kyouseipro.neo.sales.order.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.sales.order.entity.DeliveryStaffEntity;

public class DeliveryStaffParameterBinder {
    public static int bindInsert(PreparedStatement pstmt, DeliveryStaffEntity d, String editor, int index, boolean isNew) throws SQLException {
        if (isNew == false) {
            pstmt.setInt(index++, d.getOrderId());
        }
        pstmt.setInt(index++, d.getEmployeeId());
        pstmt.setInt(index++, d.getVersion());
        pstmt.setInt(index++, d.getState());

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, DeliveryStaffEntity d, String editor, int index) throws SQLException {
        pstmt.setInt(index++, d.getOrderId());
        pstmt.setInt(index++, d.getEmployeeId());
        pstmt.setInt(index++, d.getVersion());
        pstmt.setInt(index++, d.getState() +1);

        pstmt.setInt(index++, d.getDeliveryStaffId());
        pstmt.setInt(index++, d.getVersion());
        
        pstmt.setString(index++, editor);
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

    public static int bindDelete(PreparedStatement ps, int id, String editor, int index) throws SQLException {
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setString(index, editor);
        return index;
    }

    public static int setSave(PreparedStatement pstmt, List<DeliveryStaffEntity> list, String editor, int index) throws SQLException {
        for (DeliveryStaffEntity deliveryStaffEntity : list) {
            // 削除の場合
            if (deliveryStaffEntity.getState() == Enums.state.DELETE.getCode()) {
                index = DeliveryStaffParameterBinder.bindDelete(pstmt, deliveryStaffEntity.getDeliveryStaffId(), editor, index);
            } else {
                // 更新か新規かで分岐
                if (deliveryStaffEntity.getDeliveryStaffId() > 0){
                    index = DeliveryStaffParameterBinder.bindUpdate(pstmt, deliveryStaffEntity, editor, index);
                } else {
                    index = DeliveryStaffParameterBinder.bindInsert(pstmt, deliveryStaffEntity, editor, index, false);
                }
            }
        }
        return index;
    }
}
