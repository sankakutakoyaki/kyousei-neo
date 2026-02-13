package com.kyouseipro.neo.work.item.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.work.item.entity.WorkItemEntity;

public class WorkItemParameterBinder {
    public static int bindInsert(PreparedStatement pstmt, WorkItemEntity w, String editor, int index) throws SQLException {
        pstmt.setInt(index++, w.getCode());
        pstmt.setInt(index++, w.getCode());
        pstmt.setInt(index++, w.getCategoryId());
        pstmt.setString(index++, w.getName());
        pstmt.setInt(index++, w.getVersion());
        pstmt.setInt(index++, w.getState());

        pstmt.setInt(index++, w.getCategoryId());
        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, WorkItemEntity w, String editor, int index) throws SQLException {
        pstmt.setInt(index++, w.getCode());
        pstmt.setInt(index++, w.getCode());
        pstmt.setInt(index++, w.getCategoryId());
        pstmt.setString(index++, w.getName());
        pstmt.setInt(index++, w.getVersion() +1);
        pstmt.setInt(index++, w.getState());

        pstmt.setInt(index++, w.getWorkItemId());
        pstmt.setInt(index++, w.getVersion());
        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindDeleteByIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index, editor);
        return index;
    }

    public static int bindDownloadCsvByIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }
}
