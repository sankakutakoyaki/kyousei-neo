package com.kyouseipro.neo.mapper.personnel;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.personnel.WorkingConditionsListEntity;

public class WorkingConditionsListEntityMapper {
    public static WorkingConditionsListEntity map(ResultSet rs) throws SQLException {
        WorkingConditionsListEntity entity = new WorkingConditionsListEntity();
        entity.setWorking_conditions_id(rs.getInt("working_conditions_id"));
        entity.setEmployee_id(rs.getInt("employee_id"));
        entity.setCategory(rs.getInt("category"));
        entity.setFull_name(rs.getString("full_name"));
        entity.setFull_name_kana(rs.getString("full_name_kana"));
        entity.setOffice_name(rs.getString("office_name"));
        return entity;
    }
}
