package com.kyouseipro.neo.domain.work.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.domain.work.entity.WorkMasterEntity;

public class WorkMasterEntityMapper {

    public static WorkMasterEntity map(ResultSet rs) throws SQLException {

        WorkMasterEntity entity = new WorkMasterEntity();

        entity.setWorkMasterId(
            rs.getLong("work_master_id")
        );

        entity.setWorkCode(
            rs.getString("work_code")
        );

        entity.setWorkName(
            rs.getString("work_name")
        );

        entity.setWorkPrice(
            rs.getBigDecimal("work_price")
        );

        entity.setState(
            rs.getInt("state")
        );

        entity.setVersion(
            rs.getInt("version")
        );

        return entity;
    }
}