package com.kyouseipro.neo.mapper.qualification;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.qualification.QualificationMasterEntity;

public class QualificationMasterEntityMapper {
    public static QualificationMasterEntity map(ResultSet rs) throws SQLException {
        QualificationMasterEntity entity = new QualificationMasterEntity();
        entity.setQualificationMasterId(rs.getInt("qualification_master_id"));
        entity.setCode(rs.getInt("code"));
        entity.setName(rs.getString("name"));
        entity.setCategoryName(rs.getString("category_name"));
        entity.setOrganization(rs.getString("organization"));
        entity.setValidityYears(rs.getInt("validity_years"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
