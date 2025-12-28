package com.kyouseipro.neo.query.parameter.qualification;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.qualification.QualificationFilesEntity;
import com.kyouseipro.neo.interfaceis.FileUpload;

public class QualificationFilesParameterBinder {
    public static int bindInsert(
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
        return index;
    }

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

    public static int bindFindById(PreparedStatement ps, Integer qualificationFilesId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, qualificationFilesId);
        return index;
    }

    public static int bindFindAllByQualificationsId(PreparedStatement ps, Integer qualificationsId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, qualificationsId);
        return index;
    }
}

