package com.kyouseipro.neo.query.parameter.qualification;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.qualification.QualificationsEntity;

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

    public static int bindDelete(PreparedStatement pstmt, int id, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, Enums.state.DELETE.getCode());
        pstmt.setInt(index++, id);

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindFindAllByEmployeeId(PreparedStatement ps, Integer employeeId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, employeeId);
        return index;
    }

    public static int bindFindAllByCompanyId(PreparedStatement ps, Integer companyId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, companyId);
        ps.setInt(index++, Enums.clientCategory.PARTNER.getCode());
        ps.setInt(index++, Enums.clientCategory.PARTNER.getCode());
        return index;
    }

    public static int bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindAllByEmployeeStatus(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindAllByCompanyStatus(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.clientCategory.PARTNER.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.clientCategory.PARTNER.getCode());
        return index;
    }

    public static int bindDeleteForIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index, editor);
        return index;
    }

    public static int bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        return index;
    }
}
