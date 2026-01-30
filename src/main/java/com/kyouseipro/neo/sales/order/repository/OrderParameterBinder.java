package com.kyouseipro.neo.sales.order.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.sales.order.entity.DeliveryStaffEntity;
import com.kyouseipro.neo.sales.order.entity.OrderEntity;
import com.kyouseipro.neo.sales.order.entity.OrderItemEntity;
import com.kyouseipro.neo.sales.order.entity.WorkContentEntity;

public class OrderParameterBinder {
    public static int bindInsert(PreparedStatement pstmt, OrderEntity o, String editor) throws SQLException {
        int index = 1;
        pstmt.setString(index++, o.getRequestNumber());
        if (o.getStartDate() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getStartDate()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        if (o.getEndDate() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getEndDate()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        pstmt.setInt(index++, o.getPrimeConstractorId());
        pstmt.setInt(index++, o.getPrimeConstractorOfficeId());
        pstmt.setString(index++, o.getTitle());
        pstmt.setString(index++, o.getOrderPostalCode());
        pstmt.setString(index++, o.getOrderFullAddress());
        pstmt.setString(index++, o.getContactInformation());
        pstmt.setString(index++, o.getContactInformation2());
        pstmt.setString(index++, o.getRemarks());

        pstmt.setInt(index++, o.getVersion());
        pstmt.setInt(index++, o.getState());

        pstmt.setString(index++, editor);

        for (OrderItemEntity orderItemEntity : o.getItemList()) {
            index = OrderItemParameterBinder.bindInsert(pstmt, orderItemEntity, editor, index, true);
        }

        for (WorkContentEntity workContentEntity : o.getWorkList()) {
            index = WorkContentParameterBinder.bindInsert(pstmt, workContentEntity, editor, index, true);
        }

        for (DeliveryStaffEntity deliveryStaffEntity : o.getStaffList()) {
            index = DeliveryStaffParameterBinder.bindInsert(pstmt, deliveryStaffEntity, editor, index, true);
        }
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, OrderEntity o, String editor) throws SQLException {
        int index = 1;
        pstmt.setString(index++, o.getRequestNumber());
        if (o.getStartDate() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getStartDate()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        if (o.getEndDate() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getEndDate()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        pstmt.setInt(index++, o.getPrimeConstractorId());
        pstmt.setInt(index++, o.getPrimeConstractorOfficeId());
        pstmt.setString(index++, o.getTitle());
        pstmt.setString(index++, o.getOrderPostalCode());
        pstmt.setString(index++, o.getOrderFullAddress());
        pstmt.setString(index++, o.getContactInformation());
        pstmt.setString(index++, o.getContactInformation2());
        pstmt.setString(index++, o.getRemarks());

        pstmt.setInt(index++, o.getVersion() +1);
        pstmt.setInt(index++, o.getState());

        pstmt.setInt(index++, o.getOrderId()); // WHERE句
        pstmt.setInt(index++, o.getVersion());
        pstmt.setString(index++, editor);          // ログ用

        // 商品リスト
        index = OrderItemParameterBinder.setSave(pstmt, o.getItemList(), editor, index);
        // 作業リスト
        index = WorkContentParameterBinder.setSave(pstmt, o.getWorkList(), editor, index);
        // 配送担当リスト
        index = DeliveryStaffParameterBinder.setSave(pstmt, o.getStaffList(), editor, index);
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
