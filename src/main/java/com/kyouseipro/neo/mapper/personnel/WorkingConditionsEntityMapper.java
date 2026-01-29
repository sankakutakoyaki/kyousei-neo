package com.kyouseipro.neo.mapper.personnel;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;

public class WorkingConditionsEntityMapper {
    public static WorkingConditionsEntity map(ResultSet rs) throws SQLException {
        WorkingConditionsEntity entity = new WorkingConditionsEntity();
        entity.setWorkingConditionsId(rs.getInt("working_conditions_id"));
        entity.setEmployeeId(rs.getInt("employee_id"));
        entity.setOfficeName(rs.getString("office_name"));
        entity.setCode(rs.getInt("code"));
        entity.setCategory(rs.getInt("category"));
        entity.setFullName(rs.getString("full_name"));
        entity.setFullNameKana(rs.getString("full_name_kana"));
        entity.setPaymentMethod(rs.getInt("payment_method"));
        entity.setPayType(rs.getInt("pay_type"));
        entity.setBaseSalary(rs.getInt("base_salary"));
        entity.setTransCost(rs.getInt("trans_cost"));
        entity.setBasicStartTime(rs.getTime("basic_start_time").toLocalTime());
        entity.setBasicEndTime(rs.getTime("basic_end_time").toLocalTime());
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
