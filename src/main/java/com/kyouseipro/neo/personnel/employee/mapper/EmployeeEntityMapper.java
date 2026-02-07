package com.kyouseipro.neo.personnel.employee.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;

public class EmployeeEntityMapper {
    public static EmployeeEntity map(ResultSet rs) throws SQLException {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setEmployeeId(rs.getInt("employee_id"));
        entity.setCompanyId(rs.getInt("company_id"));
        entity.setCompanyName(rs.getString("company_name"));
        entity.setOfficeId(rs.getInt("office_id"));
        entity.setOfficeName(rs.getString("office_name"));
        entity.setAccount(rs.getString("account"));
        entity.setCode(rs.getInt("code"));
        entity.setCategory(rs.getInt("category"));
        entity.setLastName(rs.getString("last_name"));
        entity.setFirstName(rs.getString("first_name"));
        entity.setFullName(rs.getString("full_name"));
        entity.setLastNameKana(rs.getString("last_name_kana"));
        entity.setFirstNameKana(rs.getString("first_name_kana"));
        entity.setFullNameKana(rs.getString("full_name_kana"));
        entity.setPhoneNumber(rs.getString("phone_number"));
        entity.setPostalCode(rs.getString("postal_code"));
        entity.setFullAddress(rs.getString("full_address"));
        entity.setEmail(rs.getString("email"));
        entity.setGender(rs.getInt("gender"));
        entity.setBloodType(rs.getInt("blood_type"));
        // entity.setBirthday(rs.getDate("birthday").toLocalDate());
        entity.setBirthday(rs.getObject("birthday", LocalDate.class));
        entity.setEmergencyContact(rs.getString("emergency_contact"));
        entity.setEmergencyContactNumber(rs.getString("emergency_contact_number"));
        // entity.setDateOfHire(rs.getDate("date_of_hire").toLocalDate());
        entity.setDateOfHire(rs.getObject("date_of_hire", LocalDate.class));
        entity.setVersion(rs.getInt("version"));
        entity.setState(rs.getInt("state"));
        return entity;
    }
}
