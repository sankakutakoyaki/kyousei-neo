package com.kyouseipro.neo.mapper.personnel;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.EmployeeListEntity;

public class EmployeeListEntityMapper {
    public static EmployeeListEntity map(ResultSet rs) throws SQLException {
        EmployeeListEntity entity = new EmployeeListEntity();
        entity.setEmployee_id(rs.getInt("employee_id"));
        entity.setCode(rs.getInt("code"));
        entity.setCompany_name(rs.getString("company_name"));
        entity.setOffice_name(rs.getString("office_name"));
        entity.setFull_name(rs.getString("full_name"));
        entity.setFull_name_kana(rs.getString("full_name_kana"));
        String phone = rs.getString("phone_number");
        entity.setPhone_number(phone.isEmpty() ? null: phone);
        int category = rs.getInt("category");
        entity.setCategory(category);
        entity.setCategory_name(category == 0 ? null: Enums.employeeCategory.getDescriptionByNum(category));
        return entity;
    }
}
