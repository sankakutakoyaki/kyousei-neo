package com.kyouseipro.neo.recycle.maker.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.recycle.maker.entity.RecycleMakerEntity;

public class RecycleMakerParameterBinder {
    public static int bindInsert(PreparedStatement pstmt, RecycleMakerEntity r) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, r.getCode());
        pstmt.setString(index++, r.getName());
        pstmt.setString(index++, r.getAbbrName());
        pstmt.setInt(index++, r.getGroup());
        pstmt.setInt(index++, r.getVersion());
        pstmt.setInt(index++, r.getState());
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, RecycleMakerEntity r) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, r.getCode());
        pstmt.setString(index++, r.getName());
        pstmt.setString(index++, r.getAbbrName());
        pstmt.setInt(index++, r.getGroup());
        pstmt.setInt(index++, r.getVersion() +1);
        pstmt.setInt(index++, r.getState());
        pstmt.setInt(index++, r.getRecycleMakerId());
        pstmt.setInt(index++, r.getVersion());
        return index;
    }

    public static int bindDeleteByIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindDownloadCsvByIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }
}
