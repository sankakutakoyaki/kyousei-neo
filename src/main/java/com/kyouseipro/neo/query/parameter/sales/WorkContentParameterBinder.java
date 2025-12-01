package com.kyouseipro.neo.query.parameter.sales;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.sales.WorkContentEntity;

public class WorkContentParameterBinder {
    public static void bindInsertWorkContentParameters(PreparedStatement pstmt, WorkContentEntity w, String editor, int index, boolean isNew) throws SQLException {
        if (isNew == false) {
            pstmt.setInt(index++, w.getOrder_id());
        }
        pstmt.setString(index++, w.getWork_content());
        pstmt.setInt(index++, w.getWork_quantity());
        pstmt.setInt(index++, w.getWork_payment());

        pstmt.setInt(index++, w.getVersion());
        pstmt.setInt(index++, w.getState());

        pstmt.setString(index++, editor);
    }

    public static void bindUpdateOrderItemParameters(PreparedStatement pstmt, WorkContentEntity w, String editor, int index) throws SQLException {
        pstmt.setInt(index++, w.getOrder_id());
        pstmt.setString(index++, w.getWork_content());
        pstmt.setInt(index++, w.getWork_quantity());
        pstmt.setInt(index++, w.getWork_payment());

        pstmt.setInt(index++, w.getVersion());
        pstmt.setInt(index++, w.getState());

        pstmt.setInt(index++, w.getWork_content_id()); // WHERE句
        pstmt.setString(index++, editor);          // ログ用
    }

    public static void bindFindAllByOrderId(PreparedStatement ps, int id) throws SQLException {
        int index = 1;
        ps.setInt(index++, id);
        ps.setInt(index++, Enums.state.DELETE.getCode());
    }

    public static void bindDeleteWorkContentParameters(PreparedStatement ps, int id, String editor, int index) throws SQLException {
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setString(index, editor);
    }
}
