package com.kyouseipro.neo.query.parameter.qualification;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.qualification.QualificationsEntity;

public class QualificationsParameterBinder {

    public static void bindInsertQualificationsParameters(PreparedStatement pstmt, QualificationsEntity q, String editor) throws SQLException {
        pstmt.setInt(1, q.getOwner_id());
        pstmt.setInt(2, q.getOwner_category());
        pstmt.setInt(3, q.getQualification_master_id());
        pstmt.setString(4, q.getNumber());
        pstmt.setDate(5, Date.valueOf(q.getAcquisition_date()));
        pstmt.setDate(6, Date.valueOf(q.getExpiry_date()));
        pstmt.setInt(7, q.getVersion());
        pstmt.setInt(8, q.getState());

        pstmt.setString(9, editor);
    }

    public static void bindUpdateQualificationsParameters(PreparedStatement pstmt, QualificationsEntity q, String editor) throws SQLException {
        pstmt.setInt(1, q.getOwner_id());
        pstmt.setInt(2, q.getOwner_category());
        pstmt.setInt(3, q.getQualification_master_id());
        pstmt.setString(4, q.getNumber());
        pstmt.setDate(5, Date.valueOf(q.getAcquisition_date()));
        pstmt.setDate(6, Date.valueOf(q.getExpiry_date()));
        pstmt.setInt(7, q.getVersion());
        pstmt.setInt(8, q.getState());

        pstmt.setInt(9, q.getQualifications_id());

        pstmt.setString(10, editor);
    }

    public static void bindDeleteQualificationsParameters(PreparedStatement pstmt, int id, String editor) throws SQLException {
        pstmt.setInt(1, Enums.state.DELETE.getCode());
        pstmt.setInt(2, id);

        pstmt.setString(3, editor);
    }

    // public static void bindFindById(PreparedStatement ps, Integer qualificationsId) throws SQLException {
    //     ps.setInt(1, Enums.state.DELETE.getCode());
    //     ps.setInt(2, qualificationsId);
    // }

    public static void bindFindByIdForEmployee(PreparedStatement ps, Integer employeeId) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
        // ps.setInt(4, Enums.state.DELETE.getCode());
        ps.setInt(4, employeeId);
        // ps.setInt(5, 0);
    }

    public static void bindFindByIdForCompany(PreparedStatement ps, Integer companyId) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
        ps.setInt(4, companyId);
        ps.setInt(5, Enums.clientCategory.PARTNER.getCode());
        // ps.setInt(6, Enums.clientCategory.PARTNER.getCode());
    }

    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
    }

    public static void bindFindAllEmployeeStatus(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
        // ps.setInt(4, 0);
    }

    public static void bindFindAllCompanyStatus(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
        ps.setInt(4, Enums.clientCategory.PARTNER.getCode());
        // ps.setInt(5, Enums.clientCategory.PARTNER.getCode());
    }

    public static void bindDeleteForIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index, editor);
    }

    public static void bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
    }

    public static void bindFindAllCombo(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
    }
}
