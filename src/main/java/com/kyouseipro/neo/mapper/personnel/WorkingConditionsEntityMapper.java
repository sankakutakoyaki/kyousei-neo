package com.kyouseipro.neo.mapper.personnel;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;

public class WorkingConditionsEntityMapper {
    public static WorkingConditionsEntity map(ResultSet rs) throws SQLException {
        WorkingConditionsEntity entity = new WorkingConditionsEntity();
        entity.setWorking_conditions_id(rs.getInt("working_conditions_id"));
        entity.setEmployee_id(rs.getInt("employee_id"));
        entity.setOffice_name(rs.getString("office_name"));
        entity.setCode(rs.getInt("code"));
        entity.setCategory(rs.getInt("category"));
        entity.setFull_name(rs.getString("full_name"));
        entity.setFull_name_kana(rs.getString("full_name_kana"));
        entity.setPayment_method(rs.getInt("payment_method"));
        entity.setPay_type(rs.getInt("pay_type"));
        entity.setBase_salary(rs.getInt("base_salary"));
        entity.setTrans_cost(rs.getInt("trans_cost"));
        entity.setBasic_start_time(rs.getTime("basic_start_time").toLocalTime());
        entity.setBasic_end_time(rs.getTime("basic_end_time").toLocalTime());
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
