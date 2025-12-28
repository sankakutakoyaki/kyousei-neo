package com.kyouseipro.neo.query.parameter.order;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.order.DeliveryStaffEntity;
import com.kyouseipro.neo.entity.order.OrderEntity;
import com.kyouseipro.neo.entity.order.OrderItemEntity;
import com.kyouseipro.neo.entity.order.WorkContentEntity;

public class OrderParameterBinder {
    public static int bindInsert(PreparedStatement pstmt, OrderEntity o, String editor) throws SQLException {
        int index = 1;
        pstmt.setString(index++, o.getRequest_number());
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
            index = OrderItemParameterBinder.bindInsert(pstmt, orderItemEntity, editor, index, true);
        }

        for (WorkContentEntity workContentEntity : o.getWork_list()) {
            index = WorkContentParameterBinder.bindInsert(pstmt, workContentEntity, editor, index, true);
        }

        for (DeliveryStaffEntity deliveryStaffEntity : o.getStaff_list()) {
            index = DeliveryStaffParameterBinder.bindInsert(pstmt, deliveryStaffEntity, editor, index, true);
        }
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, OrderEntity o, String editor) throws SQLException {
        int index = 1;
        pstmt.setString(index++, o.getRequest_number());
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

        pstmt.setInt(index++, o.getVersion() +1);
        pstmt.setInt(index++, o.getState());

        pstmt.setInt(index++, o.getOrder_id()); // WHERE句
        pstmt.setInt(index++, o.getVersion());
        pstmt.setString(index++, editor);          // ログ用

        // 商品リスト
        index = OrderItemParameterBinder.setSave(pstmt, o.getItem_list(), editor, index);
        // 作業リスト
        index = WorkContentParameterBinder.setSave(pstmt, o.getWork_list(), editor, index);
        // 配送担当リスト
        index = DeliveryStaffParameterBinder.setSave(pstmt, o.getStaff_list(), editor, index);
        return index;
    }

    public static int bindFindById(PreparedStatement ps, Integer orderId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, orderId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
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
        for (Integer id : ids) {
            ps.setInt(index++, id); // company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // AND NOT (state = ?)
        return index;
    }
}
