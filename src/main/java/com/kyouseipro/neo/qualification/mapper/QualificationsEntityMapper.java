package com.kyouseipro.neo.qualification.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.qualification.entity.QualificationsEntity;

public class QualificationsEntityMapper {
    public static QualificationsEntity map(ResultSet rs) throws SQLException {
        QualificationsEntity entity = new QualificationsEntity();
        entity.setQualificationsId(rs.getInt("qualifications_id"));
        entity.setEmployeeId(rs.getInt("employee_id"));
        entity.setCompanyId(rs.getInt("company_id"));
        entity.setOwnerName(rs.getString("owner_name"));
        entity.setOwnerNameKana(rs.getString("owner_name_kana"));
        entity.setQualificationMasterId(rs.getInt("qualification_master_id"));
        entity.setQualificationName(rs.getString("qualification_name"));
        entity.setNumber(rs.getString("number"));
        entity.setAcquisitionDate(Utilities.toLocalDate(rs, "acquisition_date"));
        entity.setExpiryDate(Utilities.toLocalDate(rs, "expiry_date"));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        entity.setStatus(rs.getString("status"));
        entity.setIsEnabled(rs.getInt("is_enabled"));
        return entity;
    }
}
