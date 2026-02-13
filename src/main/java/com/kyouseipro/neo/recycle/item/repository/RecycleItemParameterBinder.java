package com.kyouseipro.neo.recycle.item.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.recycle.item.entity.RecycleItemEntity;

public class RecycleItemParameterBinder {

    public static int bindUpdate(PreparedStatement pstmt, RecycleItemEntity r, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, r.getCode());
        pstmt.setString(index++, r.getName());
        pstmt.setInt(index++, r.getVersion() +1);
        pstmt.setInt(index++, r.getState());
        pstmt.setInt(index++, r.getRecycleItemId());
        pstmt.setInt(index++, r.getVersion());
        return index;
    }

    public static int bindDelete(PreparedStatement ps, int id) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        return index;
    }

    public static int bindDeleteForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }
}
