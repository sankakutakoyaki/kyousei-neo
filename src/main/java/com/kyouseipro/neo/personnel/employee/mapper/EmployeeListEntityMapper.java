package com.kyouseipro.neo.personnel.employee.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeListEntity;

public class EmployeeListEntityMapper {
    public static EmployeeListEntity map(ResultSet rs) throws SQLException {
        EmployeeListEntity entity = new EmployeeListEntity();
        entity.setEmployeeId(rs.getInt("employee_id"));
        entity.setCode(rs.getInt("code"));
        entity.setCompanyId(rs.getInt("company_id"));
        entity.setCompanyName(rs.getString("company_name"));
        entity.setOfficeId(rs.getInt("office_id"));
        entity.setOfficeName(rs.getString("office_name"));
        entity.setFullName(rs.getString("full_name"));
        entity.setFullNameKana(rs.getString("full_name_kana"));
        entity.setPhoneNumber(rs.getString("phone_number"));
        // String phone = rs.getString("phone_number");
        // entity.setPhoneNumber(phone.isEmpty() ? null: phone);
        int category = rs.getInt("category");
        entity.setCategory(category);
        entity.setCategoryName(category == 0 ? null: Enums.employeeCategory.getDescriptionByNum(category));
        return entity;
    }
}
