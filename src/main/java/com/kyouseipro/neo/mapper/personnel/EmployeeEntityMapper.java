package com.kyouseipro.neo.mapper.personnel;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.personnel.EmployeeEntity;

public class EmployeeEntityMapper {
    public static EmployeeEntity map(ResultSet rs) throws SQLException {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setEmployee_id(rs.getInt("employee_id"));
        entity.setCompany_id(rs.getInt("company_id"));
        entity.setCompany_name(rs.getString("company_name"));
        entity.setOffice_id(rs.getInt("office_id"));
        entity.setOffice_name(rs.getString("office_name"));
        entity.setAccount(rs.getString("account"));
        entity.setCode(rs.getInt("code"));
        entity.setCategory(rs.getInt("category"));
        entity.setLast_name(rs.getString("last_name"));
        entity.setFirst_name(rs.getString("first_name"));
        entity.setFull_name(rs.getString("full_name"));
        entity.setLast_name_kana(rs.getString("last_name_kana"));
        entity.setFirst_name_kana(rs.getString("first_name_kana"));
        entity.setFull_name_kana(rs.getString("full_name_kana"));
        entity.setPhone_number(rs.getString("phone_number"));
        entity.setPostal_code(rs.getString("postal_code"));
        entity.setFull_address(rs.getString("full_address"));
        entity.setEmail(rs.getString("email"));
        entity.setGender(rs.getInt("gender"));
        entity.setBlood_type(rs.getInt("blood_type"));
        entity.setBirthday(rs.getDate("birthday").toLocalDate());
        entity.setEmergency_contact(rs.getString("emergency_contact"));
        entity.setEmergency_contact_number(rs.getString("emergency_contact_number"));
        entity.setDate_of_hire(rs.getDate("date_of_hire").toLocalDate());
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
