package com.kyouseipro.neo.qualification.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.interfaces.FileUpload;
import com.kyouseipro.neo.qualification.entity.QualificationFilesEntity;

public class QualificationFilesParameterBinder {
    public static int bindInsert(
        PreparedStatement pstmt,
        // List<FileUpload> entities,
        FileUpload e,
        String editor,
        Integer id
    ) throws SQLException {
        int index = 1;
        // for (FileUpload e : entities) {
            QualificationFilesEntity entity = (QualificationFilesEntity)e;
            pstmt.setInt(index++, id);
            pstmt.setString(index++, entity.getFileName());
            pstmt.setString(index++, entity.getInternalName());
            pstmt.setString(index++, entity.getFolderName());
            pstmt.setInt(index++, entity.getVersion());
            pstmt.setInt(index++, entity.getState());

            pstmt.setString(index++, editor);
        // }

        return index;
    }
    // public static int bindInsert(
    //     PreparedStatement pstmt,
    //     List<FileUpload> entities,
    //     String editor,
    //     Integer id
    // ) throws SQLException {
    //     int index = 1;
    //     for (FileUpload e : entities) {
    //         QualificationFilesEntity entity = (QualificationFilesEntity)e;
    //         pstmt.setInt(index++, id);
    //         pstmt.setString(index++, entity.getFile_name());
    //         pstmt.setString(index++, entity.getInternal_name());
    //         pstmt.setString(index++, entity.getFolder_name());
    //         pstmt.setInt(index++, entity.getVersion());
    //         pstmt.setInt(index++, entity.getState());

    //         pstmt.setString(index++, editor);
    //     }
    //     return index;
    // }
    public static int bindDelete(PreparedStatement pstmt, String url, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, Enums.state.DELETE.getCode());
        if (url.equals("/Users/makoto/upload/qualification/2/e1ddff60-9fd6-4eb0-8250-87064c9ff9cf.pdf")){
            System.out.println("ok:" + url);
        }
        pstmt.setString(index++, url);

        pstmt.setString(index++, editor);
        return index;
    }
}

