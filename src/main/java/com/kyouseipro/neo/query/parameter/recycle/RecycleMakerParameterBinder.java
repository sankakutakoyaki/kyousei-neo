package com.kyouseipro.neo.query.parameter.recycle;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.recycle.RecycleMakerEntity;

public class RecycleMakerParameterBinder {
    public static int bindInsertRecycleMakerParameters(PreparedStatement pstmt, RecycleMakerEntity r) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, r.getCode());
        pstmt.setString(index++, r.getName());
        pstmt.setString(index++, r.getGroup());
        pstmt.setInt(index++, r.getVersion());
        pstmt.setInt(index++, r.getState());
        return index;
    }

    public static int bindUpdateRecycleMakerParameters(PreparedStatement pstmt, RecycleMakerEntity r) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, r.getCode());
        pstmt.setString(index++, r.getName());
        pstmt.setInt(index++, r.getVersion());
        pstmt.setString(index++, r.getGroup());
        pstmt.setInt(index++, r.getState());
        pstmt.setInt(index++, r.getRecycle_maker_id());
        return index;
    }

    public static int bindFindById(PreparedStatement ps, Integer recycleMakerId) throws SQLException {
        int index = 1;
        ps.setInt(index++, recycleMakerId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindByCode(PreparedStatement ps, Integer code) throws SQLException {
        int index = 1;
        ps.setInt(index++, code);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindDeleteRecycleMakerParameters(PreparedStatement ps, int id) throws SQLException {
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
