package com.kyouseipro.neo.qualification.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.qualification.entity.QualificationsEntity;

public class QualificationsParameterBinder {

    public static int bindInsert(PreparedStatement pstmt, QualificationsEntity q, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, q.getOwnerId());
        pstmt.setInt(index++, q.getOwnerCategory());
        pstmt.setInt(index++, q.getQualificationMasterId());
        pstmt.setString(index++, q.getNumber());
        pstmt.setDate(index++, Date.valueOf(q.getAcquisitionDate()));
        pstmt.setDate(index++, Date.valueOf(q.getExpiryDate()));
        pstmt.setInt(index++, q.getVersion());
        pstmt.setInt(index++, q.getState());

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, QualificationsEntity q, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, q.getOwnerId());
        pstmt.setInt(index++, q.getOwnerCategory());
        pstmt.setInt(index++, q.getQualificationMasterId());
        pstmt.setString(index++, q.getNumber());
        pstmt.setDate(index++, Date.valueOf(q.getAcquisitionDate()));
        pstmt.setDate(index++, Date.valueOf(q.getExpiryDate()));
        pstmt.setInt(index++, q.getVersion() +1);
        pstmt.setInt(index++, q.getState());

        pstmt.setInt(index++, q.getQualificationsId());
        pstmt.setInt(index++, q.getVersion());

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindDeleteForIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index, editor);
        return index;
    }

    public static int bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index, Enums.state.DELETE.getCode());
        return index;
    }
}
