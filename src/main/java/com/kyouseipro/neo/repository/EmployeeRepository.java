package com.kyouseipro.neo.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.person.EmployeeEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmployeeRepository {
    private final SqlRepository sqlRepository;

    public boolean insertEmployee(EmployeeEntity employee) {
        String sql = "INSERT INTO employees (employee_id, company_id, office_id, account, code, category, " +
                "last_name, first_name, full_name, last_name_kana, first_name_kana, full_name_kana, " +
                "phone_number, postal_code, full_address, email, gender, blood_type, birthday, emergency_contact, " +
                "emergency_contact_number, date_of_hire, version, state) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, employee.getEmployee_id());
                pstmt.setInt(2, employee.getCompany_id());
                pstmt.setInt(3, employee.getOffice_id());
                pstmt.setString(4, employee.getAccount());
                pstmt.setInt(5, employee.getCode());
                pstmt.setInt(6, employee.getCategory());
                pstmt.setString(7, employee.getLast_name());
                pstmt.setString(8, employee.getFirst_name());
                pstmt.setString(9, employee.getFull_name());
                pstmt.setString(10, employee.getLast_name_kana());
                pstmt.setString(11, employee.getFirst_name_kana());
                pstmt.setString(12, employee.getFull_name_kana());
                pstmt.setString(13, employee.getPhone_number());
                pstmt.setString(14, employee.getPostal_code());
                pstmt.setString(15, employee.getFull_address());
                pstmt.setString(16, employee.getEmail());
                pstmt.setInt(17, employee.getGender());
                pstmt.setInt(18, employee.getBlood_type());
                pstmt.setDate(19, java.sql.Date.valueOf(employee.getBirthday()));
                pstmt.setString(20, employee.getEmergency_contact());
                pstmt.setString(21, employee.getEmergency_contact_number());
                pstmt.setDate(22, java.sql.Date.valueOf(employee.getDate_of_hire()));
                pstmt.setInt(23, employee.getVersion());
                pstmt.setInt(24, employee.getState());

                int affectedRows = pstmt.executeUpdate();
                return affectedRows == 1;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}
