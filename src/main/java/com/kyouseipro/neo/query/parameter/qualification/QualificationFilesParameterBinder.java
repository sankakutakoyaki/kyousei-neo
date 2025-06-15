package com.kyouseipro.neo.query.parameter.qualification;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.qualification.QualificationFilesEntity;
import com.kyouseipro.neo.interfaceis.FileUpload;

public class QualificationFilesParameterBinder {

    // public static void bindInsertQualificationFilesParameters(PreparedStatement pstmt, QualificationFilesEntity entity, String editor) throws SQLException {
    //     pstmt.setInt(1, entity.getQualifications_id());
    //     pstmt.setString(2, entity.getFile_name());
    //     pstmt.setString(3, entity.getInternal_name());
    //     pstmt.setString(4, entity.getFolder_name());
    //     pstmt.setInt(5, entity.getVersion());
    //     pstmt.setInt(6, entity.getState());

    //     pstmt.setString(7, editor);
    // }
    public static void bindInsertQualificationFilesParameters(
        PreparedStatement pstmt,
        List<FileUpload> entities,
        String editor,
        Integer id
    ) throws SQLException {
        int index = 1;
        for (FileUpload e : entities) {
            QualificationFilesEntity entity = (QualificationFilesEntity)e;
            pstmt.setInt(index++, id);
            pstmt.setString(index++, entity.getFile_name());
            pstmt.setString(index++, entity.getInternal_name());
            pstmt.setString(index++, entity.getFolder_name());
            pstmt.setInt(index++, entity.getVersion());
            pstmt.setInt(index++, entity.getState());

            pstmt.setString(index++, editor);
        }
    }

    // public static void bindUpdateQualificationFilesParameters(PreparedStatement pstmt, QualificationFilesEntity entity, String editor) throws SQLException {
    //     pstmt.setInt(1, entity.getQualifications_id());
    //     pstmt.setString(2, entity.getFile_name());
    //     pstmt.setString(3, entity.getInternal_name());
    //     pstmt.setString(4, entity.getFolder_name());
    //     pstmt.setInt(5, entity.getVersion());
    //     pstmt.setInt(6, entity.getState());

    //     pstmt.setInt(7, entity.getQualifications_files_id());

    //     pstmt.setString(8, editor);
    // }

    public static void bindDeleteQualificationFilesParameters(PreparedStatement pstmt, String url, String editor) throws SQLException {
        pstmt.setInt(1, Enums.state.DELETE.getCode());
        if (url.equals("/Users/makoto/upload/qualification/2/e1ddff60-9fd6-4eb0-8250-87064c9ff9cf.pdf")){
            System.out.println("ok:" + url);
        }
        pstmt.setString(2, url);

        pstmt.setString(3, editor);
    }

    public static void bindFindByQualificationsFIlesId(PreparedStatement ps, Integer qualificationFilesId) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, qualificationFilesId);
    }

    public static void bindFindAllByQualificationsId(PreparedStatement ps, Integer qualificationsId) throws SQLException {
        
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, qualificationsId);
    }
}

