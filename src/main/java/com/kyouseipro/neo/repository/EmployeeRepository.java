package com.kyouseipro.neo.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.person.EmployeeEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmployeeRepository {
    private final SqlRepository sqlRepository;
    private final GenericRepository genericRepository;

    public Integer insertEmployee(EmployeeEntity employee, String editor) {
        String sql = 
            "DECLARE @InsertedRows TABLE (employee_id INT);" +
            "INSERT INTO employees (employe_id, office_id, account, code, category, " +
            "last_name, first_name, full_name, last_name_kana, first_name_kana, full_name_kana, " +
            "phone_number, postal_code, full_address, email, gender, blood_type, birthday, emergency_contact, " +
            "emergency_contact_number, date_of_hire, version, state) " +
            "OUTPUT INSERTED.employee_id INTO @InsertedRows " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);" +
            "INSERT INTO employees_log (employee_id, editor, process, log_date, " +
            "employe_id, office_id, account, code, category, last_name, first_name, full_name, last_name_kana, first_name_kana, full_name_kana, " +
            "phone_number, postal_code, full_address, email, gender, blood_type, birthday, emergency_contact, emergency_contact_number, date_of_hire, version, state) " +
            "SELECT e.employee_id, ?, 'INSERT', CURRENT_TIMESTAMP, " +
            "e.employe_id, e.office_id, e.account, e.code, e.category, e.last_name, e.first_name, e.full_name, e.last_name_kana, e.first_name_kana, e.full_name_kana, " +
            "e.phone_number, e.postal_code, e.full_address, e.email, e.gender, e.blood_type, e.birthday, e.emergency_contact, e.emergency_contact_number, e.date_of_hire, e.version, e.state " +
            "FROM employees e " +
            "INNER JOIN @InsertedRows ir ON e.employee_id = ir.employee_id;" +
            "SELECT employee_id FROM @InsertedRows;";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, employee.getCompany_id());
                pstmt.setInt(2, employee.getOffice_id());
                pstmt.setString(3, employee.getAccount());
                pstmt.setInt(4, employee.getCode());
                pstmt.setInt(5, employee.getCategory());
                pstmt.setString(6, employee.getLast_name());
                pstmt.setString(7, employee.getFirst_name());
                pstmt.setString(8, employee.getFull_name());
                pstmt.setString(9, employee.getLast_name_kana());
                pstmt.setString(10, employee.getFirst_name_kana());
                pstmt.setString(11, employee.getFull_name_kana());
                pstmt.setString(12, employee.getPhone_number());
                pstmt.setString(13, employee.getPostal_code());
                pstmt.setString(14, employee.getFull_address());
                pstmt.setString(15, employee.getEmail());
                pstmt.setInt(16, employee.getGender());
                pstmt.setInt(17, employee.getBlood_type());

                if (employee.getBirthday() != null) {
                    pstmt.setDate(18, java.sql.Date.valueOf(employee.getBirthday()));
                } else {
                    pstmt.setNull(18, java.sql.Types.DATE);
                }

                pstmt.setString(19, employee.getEmergency_contact());
                pstmt.setString(20, employee.getEmergency_contact_number());

                if (employee.getDate_of_hire() != null) {
                    pstmt.setDate(21, java.sql.Date.valueOf(employee.getDate_of_hire()));
                } else {
                    pstmt.setNull(21, java.sql.Types.DATE);
                }

                pstmt.setInt(22, employee.getVersion());
                pstmt.setInt(23, employee.getState());

                pstmt.setString(24, editor);

                boolean hasResultSet = pstmt.execute();

                if (hasResultSet) {
                    try (ResultSet rs = pstmt.getResultSet()) {
                        if (rs.next()) {
                            return rs.getInt("employee_id");
                        }
                    }
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public Integer updateEmployee(EmployeeEntity employee, String editor) {
        String sql = 
            "DECLARE @UpdatedRows TABLE (employee_id INT);" +
            "UPDATE employees SET " +
            "employe_id=?, office_id=?, account=?, code=?, category=?, " +
            "last_name=?, first_name=?, full_name=?, last_name_kana=?, first_name_kana=?, full_name_kana=?, " +
            "phone_number=?, postal_code=?, full_address=?, email=?, gender=?, blood_type=?, birthday=?, emergency_contact=?, " +
            "emergency_contact_number=?, date_of_hire=?, version=?, state=? " +
            "OUTPUT INSERTED.employee_id INTO @UpdatedRows " +
            "WHERE employee_id=?;" +
            "INSERT INTO employees_log (employee_id, editor, process, log_date, " +
            "employe_id, office_id, account, code, category, last_name, first_name, full_name, last_name_kana, first_name_kana, full_name_kana, " +
            "phone_number, postal_code, full_address, email, gender, blood_type, birthday, emergency_contact, emergency_contact_number, date_of_hire, version, state) " +
            "SELECT e.employee_id, ?, 'UPDATE', CURRENT_TIMESTAMP, " +
            "e.employe_id, e.office_id, e.account, e.code, e.category, e.last_name, e.first_name, e.full_name, e.last_name_kana, e.first_name_kana, e.full_name_kana, " +
            "e.phone_number, e.postal_code, e.full_address, e.email, e.gender, e.blood_type, e.birthday, e.emergency_contact, e.emergency_contact_number, e.date_of_hire, e.version, e.state " +
            "FROM employees e " +
            "INNER JOIN @UpdatedRows ur ON e.employee_id = ur.employee_id;" +
            "SELECT employee_id FROM @UpdatedRows;";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, employee.getCompany_id());
                pstmt.setInt(2, employee.getOffice_id());
                pstmt.setString(3, employee.getAccount());
                pstmt.setInt(4, employee.getCode());
                pstmt.setInt(5, employee.getCategory());
                pstmt.setString(6, employee.getLast_name());
                pstmt.setString(7, employee.getFirst_name());
                pstmt.setString(8, employee.getFull_name());
                pstmt.setString(9, employee.getLast_name_kana());
                pstmt.setString(10, employee.getFirst_name_kana());
                pstmt.setString(11, employee.getFull_name_kana());
                pstmt.setString(12, employee.getPhone_number());
                pstmt.setString(13, employee.getPostal_code());
                pstmt.setString(14, employee.getFull_address());
                pstmt.setString(15, employee.getEmail());
                pstmt.setInt(16, employee.getGender());
                pstmt.setInt(17, employee.getBlood_type());

                if (employee.getBirthday() != null) {
                    pstmt.setDate(18, java.sql.Date.valueOf(employee.getBirthday()));
                } else {
                    pstmt.setNull(18, java.sql.Types.DATE);
                }

                pstmt.setString(19, employee.getEmergency_contact());
                pstmt.setString(20, employee.getEmergency_contact_number());

                if (employee.getDate_of_hire() != null) {
                    pstmt.setDate(21, java.sql.Date.valueOf(employee.getDate_of_hire()));
                } else {
                    pstmt.setNull(21, java.sql.Types.DATE);
                }

                pstmt.setInt(22, employee.getVersion());
                pstmt.setInt(23, employee.getState());

                pstmt.setInt(24, employee.getEmployee_id());  // WHERE句のID
                pstmt.setString(25, editor); // ログ用のエディタ名

                boolean hasResultSet = pstmt.execute();

                if (hasResultSet) {
                    try (ResultSet rs = pstmt.getResultSet()) {
                        if (rs.next()) {
                            return rs.getInt("employee_id");
                        }
                    }
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    // employe_idによる取得
    public EmployeeEntity findById(int employeeId) {
        return genericRepository.findOne(
        "SELECT * FROM employees WHERE employee_id = ? AND NOT (state = ?)",
            ps -> {
                ps.setInt(1, employeeId); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
            },
            EmployeeEntity::new // Supplier<T>
        );
    }

    // 全件取得の例（必要に応じて）
    public List<EmployeeEntity> findAll() {
        return genericRepository.findAll(
            "SELECT * FROM employees WHERE NOT (state = ?)",
            ps -> ps.setInt(1, Enums.state.DELETE.getCode()),
            EmployeeEntity::new
        );
    }
}
