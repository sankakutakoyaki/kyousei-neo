package com.kyouseipro.neo.mapper.qualification;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.qualification.QualificationsEntity;

public class QualificationsEntityMapper {
    public static QualificationsEntity map(ResultSet rs) throws SQLException {
        QualificationsEntity entity = new QualificationsEntity();
        entity.setQualificationsId(rs.getInt("qualifications_id"));
        entity.setOwnerId(rs.getInt("owner_id"));
        entity.setOwnerCategory(rs.getInt("owner_category"));
        entity.setOwnerName(rs.getString("owner_name"));
        entity.setOwnerNameKana(rs.getString("owner_name_kana"));
        entity.setQualificationMasterId(rs.getInt("qualification_master_id"));
        entity.setQualificationName(rs.getString("qualification_name"));
        entity.setNumber(rs.getString("number"));
        entity.setAcquisitionDate(rs.getDate("acquisition_date").toLocalDate());
        entity.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        entity.setStatus(rs.getString("status"));
        entity.setIsEnabled(rs.getInt("is_enabled"));
        return entity;
    }
}
