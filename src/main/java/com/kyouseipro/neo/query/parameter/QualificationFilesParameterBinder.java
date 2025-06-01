package com.kyouseipro.neo.query.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.common.QualificationFilesEntity;

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
}

