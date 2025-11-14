package com.kyouseipro.neo.query.parameter.sales;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.sales.OrderEntity;

public class OrderParameterBinder {
    public static void bindInsertOrderParameters(PreparedStatement pstmt, OrderEntity o, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, o.getOrder_id());
        pstmt.setString(index++, o.getRequest_number());
        if (o.getOrder_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getOrder_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
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
        pstmt.setString(index++, o.getTitle());
        pstmt.setString(index++, o.getOrder_postal_code());
        pstmt.setString(index++, o.getOrder_full_address());

        pstmt.setInt(index++, o.getVersion());
        pstmt.setInt(index++, o.getState());

        pstmt.setString(index++, editor);
    }

    public static void bindUpdateOrderParameters(PreparedStatement pstmt, OrderEntity o, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, o.getOrder_id());
        pstmt.setString(index++, o.getRequest_number());
        if (o.getOrder_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(o.getOrder_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
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
        pstmt.setString(index++, o.getTitle());
        pstmt.setString(index++, o.getOrder_postal_code());
        pstmt.setString(index++, o.getOrder_full_address());

        pstmt.setInt(index++, o.getVersion());
        pstmt.setInt(index++, o.getState());

        pstmt.setInt(index++, o.getOrder_id()); // WHERE句
        pstmt.setString(index++, editor);          // ログ用
    }

    public static void bindFindById(PreparedStatement ps, Integer orderId) throws SQLException {
        ps.setInt(1, orderId);
        ps.setInt(2, Enums.state.DELETE.getCode());
    }

    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
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
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
    }
}
