package com.kyouseipro.neo.qualification.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.corporation.company.entity.CompanyEntityRequest;
import com.kyouseipro.neo.qualification.entity.QualificationsEntityRequest;
import com.kyouseipro.neo.qualification.entity.QualificationsEntity;

public class QualificationsParameterBinder {

    // public static int bindInsert(PreparedStatement pstmt, QualificationsEntity q, String editor) throws SQLException {
    //     int index = 1;
    //     pstmt.setInt(index++, q.getEmployeeId());
    //     pstmt.setInt(index++, q.getCompanyId());
    //     pstmt.setInt(index++, q.getQualificationMasterId());
    //     pstmt.setString(index++, q.getNumber());
    //     pstmt.setDate(index++, Date.valueOf(q.getAcquisitionDate()));
    //     pstmt.setDate(index++, Date.valueOf(q.getExpiryDate()));
    //     pstmt.setInt(index++, q.getVersion());
    //     pstmt.setInt(index++, q.getState());

    //     pstmt.setString(index++, editor);
    //     return index;
    // }

    // public static int bindUpdate(PreparedStatement pstmt, QualificationsEntity q, String editor) throws SQLException {
    //     int index = 1;
    //     pstmt.setInt(index++, q.getEmployeeId());
    //     pstmt.setInt(index++, q.getCompanyId());
    //     pstmt.setInt(index++, q.getQualificationMasterId());
    //     pstmt.setString(index++, q.getNumber());
    //     pstmt.setDate(index++, Date.valueOf(q.getAcquisitionDate()));
    //     pstmt.setDate(index++, Date.valueOf(q.getExpiryDate()));
    //     pstmt.setInt(index++, q.getVersion() +1);
    //     pstmt.setInt(index++, q.getState());

    //     pstmt.setInt(index++, q.getQualificationsId());
    //     pstmt.setInt(index++, q.getVersion());

    //     pstmt.setString(index++, editor);
    //     return index;
    // }
    private static int bindSaveParameter (PreparedStatement ps, QualificationsEntityRequest req, String editor, int idx) throws SQLException {
        
        if (req.getEmployeeId() != null) {
            ps.setInt(idx++, req.getEmployeeId());
        }

        if (req.getCompanyId() != null) {
            ps.setInt(idx++, req.getCompanyId());
        }
        if (req.getQualificationMasterId() != null) {
            ps.setInt(idx++, req.getQualificationMasterId());
        }

        if (req.getNumber() != null) {
            ps.setString(idx++, req.getNumber());
        }
        if (req.getAcquisitionDate() != null) {
            ps.setDate(idx++, Date.valueOf(req.getAcquisitionDate()));
        }
        if (req.getExpiryDate() != null) {
            ps.setDate(idx++, Date.valueOf(req.getExpiryDate()));
        }
        
        return idx;
    }

    public static void bindBulkInsert(PreparedStatement ps, QualificationsEntityRequest req, String editor) throws SQLException {

        int idx = bindSaveParameter(ps, req, editor, 1);

        // ログ用
        ps.setString(idx++, editor);          
    }

    public static void bindBulkUpdate(PreparedStatement ps, QualificationsEntityRequest req, String editor) throws SQLException {

        int idx = bindSaveParameter(ps, req, editor, 1);

        // WHERE
        ps.setInt(idx++, req.getQualificationsId());
        ps.setInt(idx++, req.getVersion());
        ps.setInt(idx++, Enums.state.DELETE.getCode());

        // ログ用
        ps.setString(idx++, editor);          
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
