package com.kyouseipro.neo.mapper.personnel;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.EmployeeListEntity;

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
        String phone = rs.getString("phone_number");
        entity.setPhone_number(phone.isEmpty() ? null: phone);
        int category = rs.getInt("category");
        entity.setCategory(category);
        entity.setCategoryName(category == 0 ? null: Enums.employeeCategory.getDescriptionByNum(category));
        return entity;
    }
}
