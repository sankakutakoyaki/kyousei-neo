package com.kyouseipro.neo.qualification.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.qualification.entity.QualificationsDto;

public class QualificationsDtoMapper {
    public static QualificationsDto map(ResultSet rs) throws SQLException {
        QualificationsDto entity = new QualificationsDto();
        entity.setQualificationsId(rs.getLong("qualifications_id"));
        entity.setQualificationName(rs.getString("qualification_name"));
        entity.setNumber(rs.getString("number"));
        return entity;
    }
}
