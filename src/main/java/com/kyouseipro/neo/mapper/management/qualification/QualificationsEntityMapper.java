package com.kyouseipro.neo.mapper.management.qualification;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.management.qualification.QualificationsEntity;

public class QualificationsEntityMapper {
    public static QualificationsEntity map(ResultSet rs) throws SQLException {
        QualificationsEntity entity = new QualificationsEntity();
        entity.setQualifications_id(rs.getInt("qualifications_id"));
        entity.setOwner_id(rs.getInt("owner_id"));
        entity.setOwner_category(rs.getInt("owner_category"));
        entity.setOwner_name(rs.getString("owner_name"));
        entity.setOwner_name_kana(rs.getString("owner_name_kana"));
        entity.setQualification_master_id(rs.getInt("qualification_master_id"));
        entity.setQualification_name(rs.getString("qualification_name"));
        entity.setNumber(rs.getString("number"));
        entity.setAcquisition_date(rs.getDate("acquisition_date").toLocalDate());
        entity.setExpiry_date(rs.getDate("expiry_date").toLocalDate());
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        entity.setStatus(rs.getString("status"));
        entity.setIs_enabled(rs.getInt("is_enabled"));
        return entity;
    }
}
