package com.kyouseipro.neo.qualification.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.qualification.entity.QualificationFilesEntity;

public class QualificationFilesEntityMapper {
     public static QualificationFilesEntity map(ResultSet rs) throws SQLException {
        QualificationFilesEntity entity = new QualificationFilesEntity();
        entity.setQualificationsFilesId(rs.getInt("qualifications_files_id"));
        entity.setQualificationsId(rs.getInt("qualifications_id"));
        entity.setFileName(rs.getString("file_name"));
        entity.setInternalName(rs.getString("internal_name"));
        entity.setFolderName(rs.getString("folder_name"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }   
}
