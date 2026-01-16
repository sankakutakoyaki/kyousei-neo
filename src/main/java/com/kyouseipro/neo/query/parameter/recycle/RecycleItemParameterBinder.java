package com.kyouseipro.neo.query.parameter.recycle;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.recycle.RecycleItemEntity;

public class RecycleItemParameterBinder {
    public static int bindInsert(PreparedStatement pstmt, RecycleItemEntity r) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, r.getCode());
        pstmt.setString(index++, r.getName());
        pstmt.setInt(index++, r.getVersion());
        pstmt.setInt(index++, r.getState());
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, RecycleItemEntity r, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, r.getCode());
        pstmt.setString(index++, r.getName());
        pstmt.setInt(index++, r.getVersion() +1);
        pstmt.setInt(index++, r.getState());
        pstmt.setInt(index++, r.getRecycle_item_id());
        pstmt.setInt(index++, r.getVersion());
        return index;
    }

    public static int bindFindById(PreparedStatement ps, Integer recycleItemId) throws SQLException {
        int index = 1;
        ps.setInt(index++, recycleItemId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }
    
    public static int bindFindByCode(PreparedStatement ps, Integer code) throws SQLException {
        int index = 1;
        ps.setInt(index++, code);
        ps.setInt(index++, Enums.state.DELETE.getCode());
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
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        return index;
    }

    public static int bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        for (Integer id : ids) {
            ps.setInt(index++, id); // id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }
}
