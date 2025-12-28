package com.kyouseipro.neo.query.parameter.order;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.order.WorkContentEntity;

public class WorkContentParameterBinder {
    public static int bindInsertWorkContentParameters(PreparedStatement pstmt, WorkContentEntity w, String editor, int index, boolean isNew) throws SQLException {
        if (isNew == false) {
            pstmt.setInt(index++, w.getOrder_id());
        }
        pstmt.setString(index++, w.getWork_content());
        pstmt.setInt(index++, w.getWork_quantity());
        pstmt.setInt(index++, w.getWork_payment());

        pstmt.setInt(index++, w.getVersion());
        pstmt.setInt(index++, w.getState());

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindUpdateOrderItemParameters(PreparedStatement pstmt, WorkContentEntity w, String editor, int index) throws SQLException {
        pstmt.setInt(index++, w.getOrder_id());
        pstmt.setString(index++, w.getWork_content());
        pstmt.setInt(index++, w.getWork_quantity());
        pstmt.setInt(index++, w.getWork_payment());

        pstmt.setInt(index++, w.getVersion());
        pstmt.setInt(index++, w.getState());

        pstmt.setInt(index++, w.getWork_content_id()); // WHERE句
        pstmt.setString(index++, editor);          // ログ用
        return index;
    }

    public static int bindFindAllByOrderId(PreparedStatement ps, int id) throws SQLException {
        int index = 1;
        ps.setInt(index++, id);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindDeleteWorkContentParameters(PreparedStatement ps, int id, String editor, int index) throws SQLException {
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setString(index, editor);
        return index;
    }

    public static int setSaveWorkContentListParameters(PreparedStatement pstmt, List<WorkContentEntity> list, String editor, int index) throws SQLException {
        for (WorkContentEntity workContentEntity : list) {
            // 削除の場合
            if (workContentEntity.getState() == Enums.state.DELETE.getCode()) {
                index = WorkContentParameterBinder.bindDeleteWorkContentParameters(pstmt, workContentEntity.getWork_content_id(), editor, index);
                // index = index + 3;
            } else {
                // 更新か新規かで分岐
                if (workContentEntity.getWork_content_id() > 0){
                    index = WorkContentParameterBinder.bindUpdateOrderItemParameters(pstmt, workContentEntity, editor, index);
                    // index = index + 8;
                } else {
                    index = WorkContentParameterBinder.bindInsertWorkContentParameters(pstmt, workContentEntity, editor, index, false);
                    // index = index + 7;
                }
            }
        }
        return index;
    }
}
