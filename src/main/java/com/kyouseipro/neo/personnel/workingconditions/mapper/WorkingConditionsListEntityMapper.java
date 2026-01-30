package com.kyouseipro.neo.personnel.workingconditions.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.personnel.workingconditions.entity.WorkingConditionsListEntity;

public class WorkingConditionsListEntityMapper {
    public static WorkingConditionsListEntity map(ResultSet rs) throws SQLException {
        WorkingConditionsListEntity entity = new WorkingConditionsListEntity();
        entity.setWorkingConditionsId(rs.getInt("working_conditions_id"));
        entity.setEmployeeId(rs.getInt("employee_id"));
        entity.setCategory(rs.getInt("category"));
        entity.setFullName(rs.getString("full_name"));
        entity.setFullNameKana(rs.getString("full_name_kana"));
        entity.setOfficeName(rs.getString("office_name"));
        return entity;
    }
}
