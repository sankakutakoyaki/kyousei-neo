package com.kyouseipro.neo.query.parameter.qualification;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.qualification.QualificationFilesEntity;

public class QualificationFilesParameterBinder {

    public static void bindInsertQualificationFilesParameters(PreparedStatement pstmt, QualificationFilesEntity entity, String editor) throws SQLException {
        pstmt.setInt(1, entity.getQualifications_id());
        pstmt.setString(2, entity.getFile_name());
        pstmt.setString(3, entity.getInternal_name());
        pstmt.setString(4, entity.getFolder_name());
        pstmt.setInt(5, entity.getVersion());
        pstmt.setInt(6, entity.getState());

        pstmt.setString(7, editor);
    }

    public static void bindUpdateQualificationFilesParameters(PreparedStatement pstmt, QualificationFilesEntity entity, String editor) throws SQLException {
        pstmt.setInt(1, entity.getQualifications_id());
        pstmt.setString(2, entity.getFile_name());
        pstmt.setString(3, entity.getInternal_name());
        pstmt.setString(4, entity.getFolder_name());
        pstmt.setInt(5, entity.getVersion());
        pstmt.setInt(6, entity.getState());

        pstmt.setInt(7, entity.getQualifications_files_id());

        pstmt.setString(8, editor);
    }

    public static void bindDeleteQualificationFilesParameters(PreparedStatement pstmt, String url, String editor) throws SQLException {
        pstmt.setInt(1, Enums.state.DELETE.getCode());
        pstmt.setString(2, url);

        pstmt.setString(3, editor);
    }

    public static void bindFindById(PreparedStatement ps, Integer qualificationFilesId) throws SQLException {
        ps.setInt(1, qualificationFilesId);
        ps.setInt(2, Enums.state.DELETE.getCode());
    }

    public static void bindFindAllByQualificationsId(PreparedStatement ps, Integer qualificationsId) throws SQLException {
        ps.setInt(1, qualificationsId);
        ps.setInt(2, Enums.state.DELETE.getCode());
    }
}

