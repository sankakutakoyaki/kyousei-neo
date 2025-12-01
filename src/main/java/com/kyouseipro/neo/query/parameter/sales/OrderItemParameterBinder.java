package com.kyouseipro.neo.query.parameter.sales;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.sales.OrderItemEntity;

public class OrderItemParameterBinder {
    public static void bindInsertOrderItemParameters(PreparedStatement pstmt, OrderItemEntity o, String editor, int index, boolean isNew) throws SQLException {
        // int index = 1;
        // pstmt.setInt(index++, o.getOrder_id());
        // if (o.getOrder_id() > 0){
        //     pstmt.setInt(index++, o.getOrder_id());
        // }
        if (isNew == false) {
            pstmt.setInt(index++, o.getOrder_id());
        }
        pstmt.setString(index++, o.getItem_maker());
        pstmt.setString(index++, o.getItem_name());
        pstmt.setString(index++, o.getItem_model());
        pstmt.setInt(index++, o.getItem_quantity());
        if (o.getArrival_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getArrival_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }

        pstmt.setInt(index++, o.getVersion());
        pstmt.setInt(index++, o.getState());

        pstmt.setString(index++, editor);
    }

    public static void bindUpdateOrderItemParameters(PreparedStatement pstmt, OrderItemEntity o, String editor, int index) throws SQLException {
        // int index = 1;
        // pstmt.setInt(index++, o.getOrder_item_id());
        pstmt.setInt(index++, o.getOrder_id());
        pstmt.setString(index++, o.getItem_maker());
        pstmt.setString(index++, o.getItem_name());
        pstmt.setString(index++, o.getItem_model());
        pstmt.setInt(index++, o.getItem_quantity());
        if (o.getArrival_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getArrival_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }

        pstmt.setInt(index++, o.getVersion());
        pstmt.setInt(index++, o.getState());

        pstmt.setInt(index++, o.getOrder_item_id()); // WHERE句
        pstmt.setString(index++, editor);          // ログ用
    }

    public static void bindFindById(PreparedStatement ps, Integer orderItemId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, orderItemId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
    }

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
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setInt(index++, Enums.state.DELETE.getCode());
    }

    public static void bindDeleteOrderItemParameters(PreparedStatement ps, int id, String editor, int index) throws SQLException {
        // int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setString(index, editor);
    }

    public static void bindFindByBetweenOrderItemEntity(PreparedStatement ps, LocalDate start, LocalDate end) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, start.toString());
        ps.setString(index++, end.toString());
        ps.setString(index++, start.toString());
        ps.setString(index++, end.toString());
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
}
