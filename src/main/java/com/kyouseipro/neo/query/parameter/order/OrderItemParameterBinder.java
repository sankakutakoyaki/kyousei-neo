package com.kyouseipro.neo.query.parameter.order;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.order.OrderItemEntity;

public class OrderItemParameterBinder {
    public static int bindInsert(PreparedStatement pstmt, OrderItemEntity o, String editor, int index, boolean isNew) throws SQLException {
        if (isNew == false) {
            pstmt.setInt(index++, o.getOrder_id());
        }
        pstmt.setInt(index++, o.getCompany_id());
        pstmt.setInt(index++, o.getOffice_id());
        pstmt.setString(index++, o.getDelivery_address());
        if (o.getArrival_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getArrival_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        pstmt.setInt(index++, o.getInspector_id());
        pstmt.setInt(index++, o.getShipping_company_id());
        pstmt.setString(index++, o.getDocument_number());
        pstmt.setString(index++, o.getItem_maker());
        pstmt.setString(index++, o.getItem_name());
        pstmt.setString(index++, o.getItem_model());
        pstmt.setInt(index++, o.getItem_quantity());
        pstmt.setInt(index++, o.getItem_payment());
        pstmt.setInt(index++, o.getBuyer_id());
        pstmt.setString(index++, o.getRemarks());
        pstmt.setInt(index++, o.getClassification());

        pstmt.setInt(index++, o.getVersion());
        pstmt.setInt(index++, o.getState());

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, OrderItemEntity o, String editor, int index) throws SQLException {
        pstmt.setInt(index++, o.getOrder_id());
        pstmt.setInt(index++, o.getCompany_id());
        pstmt.setInt(index++, o.getOffice_id());
        pstmt.setString(index++, o.getDelivery_address());
        if (o.getArrival_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getArrival_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        pstmt.setInt(index++, o.getInspector_id());
        pstmt.setInt(index++, o.getShipping_company_id());
        pstmt.setString(index++, o.getDocument_number());
        pstmt.setString(index++, o.getItem_maker());
        pstmt.setString(index++, o.getItem_name());
        pstmt.setString(index++, o.getItem_model());
        pstmt.setInt(index++, o.getItem_quantity());
        pstmt.setInt(index++, o.getItem_payment());
        pstmt.setInt(index++, o.getBuyer_id());
        pstmt.setString(index++, o.getRemarks());
        pstmt.setInt(index++, o.getClassification());

        pstmt.setInt(index++, o.getVersion());
        pstmt.setInt(index++, o.getState());

        pstmt.setInt(index++, o.getOrder_item_id()); // WHERE句
        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindFindById(PreparedStatement ps, Integer orderItemId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, orderItemId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindAllByOrderId(PreparedStatement ps, int id) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindDelete(PreparedStatement ps, int id, String editor, int index) throws SQLException {
        // int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setString(index, editor);
        return index;
    }

    public static int bindFindByBetween(PreparedStatement ps, LocalDate start, LocalDate end) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, start.toString());
        ps.setString(index++, end.toString());
        ps.setString(index++, start.toString());
        ps.setString(index++, end.toString());
        return index;
    }

    public static int bindSave(PreparedStatement pstmt, List<OrderItemEntity> list, String editor) throws SQLException {
        int index = 1;
        index = setSave(pstmt, list, editor, index);
        return index;
    }

    public static int setSave(PreparedStatement pstmt, List<OrderItemEntity> list, String editor, int index) throws SQLException {
        for (OrderItemEntity entity : list) {
            // 削除の場合
            if (entity.getState() == Enums.state.DELETE.getCode()) {
                index = OrderItemParameterBinder.bindDelete(pstmt, entity.getOrder_item_id(), editor, index);
            } else {
                // 更新か新規かで分岐
                if (entity.getOrder_item_id() > 0){
                    index = OrderItemParameterBinder.bindUpdate(pstmt, entity, editor, index);
                } else {
                    index = OrderItemParameterBinder.bindInsert(pstmt, entity, editor, index, false);
                }
            }
        }
        return index;
    }

    public static int bindDeleteByIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index, editor); // 4. log
        return index;
    }

    public static int bindDownloadCsvByIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id); // id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }
}
