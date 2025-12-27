package com.kyouseipro.neo.query.parameter.sales;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.order.DeliveryStaffEntity;
import com.kyouseipro.neo.entity.order.OrderEntity;
import com.kyouseipro.neo.entity.order.OrderItemEntity;
import com.kyouseipro.neo.entity.order.WorkContentEntity;

public class OrderParameterBinder {
    public static int bindInsertOrderParameters(PreparedStatement pstmt, OrderEntity o, String editor) throws SQLException {
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
            index = OrderItemParameterBinder.bindInsertOrderItemParameters(pstmt, orderItemEntity, editor, index, true);
            // index = index + 18;
        }

        for (WorkContentEntity workContentEntity : o.getWork_list()) {
            index = WorkContentParameterBinder.bindInsertWorkContentParameters(pstmt, workContentEntity, editor, index, true);
            // index = index + 6;
        }

        for (DeliveryStaffEntity deliveryStaffEntity : o.getStaff_list()) {
            index = DeliveryStaffParameterBinder.bindInsertDeliveryStaffParameters(pstmt, deliveryStaffEntity, editor, index, true);
            // index = index + 4;
        }
        return index;
    }

    public static int bindUpdateOrderParameters(PreparedStatement pstmt, OrderEntity o, String editor) throws SQLException {
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

        pstmt.setInt(index++, o.getOrder_id()); // WHERE句
        pstmt.setString(index++, editor);          // ログ用

        // 商品リスト
        index = OrderItemParameterBinder.setSaveOrderItemListParameters(pstmt, o.getItem_list(), editor, index);
        // 作業リスト
        index = WorkContentParameterBinder.setSaveWorkContentListParameters(pstmt, o.getWork_list(), editor, index);
        // 配送担当リスト
        index = DeliveryStaffParameterBinder.setSaveDeliveryStaffListParameters(pstmt, o.getStaff_list(), editor, index);
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

    public static int bindDeleteForIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index, editor); // 4. log
        return index;
    }

    public static int bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
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
