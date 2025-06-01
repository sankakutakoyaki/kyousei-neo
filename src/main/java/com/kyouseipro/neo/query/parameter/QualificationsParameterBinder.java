package com.kyouseipro.neo.query.parameter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.common.QualificationsEntity;

public class QualificationsParameterBinder {

    public static void bindInsertQualificationsParameters(PreparedStatement pstmt, QualificationsEntity q, String editor) throws SQLException {
        pstmt.setInt(1, q.getOwner_id());
        pstmt.setInt(2, q.getQualification_master_id());
        pstmt.setString(3, q.getNumber());
        pstmt.setDate(4, Date.valueOf(q.getAcquisition_date()));
        pstmt.setDate(5, Date.valueOf(q.getExpiry_date()));
        pstmt.setInt(6, q.getVersion());
        pstmt.setInt(7, q.getState());

        pstmt.setString(8, editor);
    }

    public static void bindUpdateQualificationsParameters(PreparedStatement pstmt, QualificationsEntity q, String editor) throws SQLException {
        pstmt.setInt(1, q.getOwner_id());
        pstmt.setInt(2, q.getQualification_master_id());
        pstmt.setString(3, q.getNumber());
        pstmt.setDate(4, Date.valueOf(q.getAcquisition_date()));
        pstmt.setDate(5, Date.valueOf(q.getExpiry_date()));
        pstmt.setInt(6, q.getVersion());
        pstmt.setInt(7, q.getState());

        pstmt.setInt(8, q.getQualifications_id());

        pstmt.setString(9, editor);
    }
}
