package com.kyouseipro.neo.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.StaffEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StaffRepository {
    private final SqlRepository sqlRepository;
    private final GenericRepository genericRepository;

    // 新規登録
    public Integer insertStaff(StaffEntity staff, String editor) {
        String sql = 
            "DECLARE @InsertedRows TABLE (staff_id INT);" +
            "INSERT INTO staffs (company_id, office_id, company_name, office_name, name, name_kana, phone_number, email, version, state) " +
            "OUTPUT INSERTED.staff_id INTO @InsertedRows " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);" +
            "INSERT INTO staffs_log (staff_id, editor, process, log_date, company_id, office_id, company_name, office_name, name, name_kana, phone_number, email, version, state) " +
            "SELECT s.staff_id, ?, 'INSERT', CURRENT_TIMESTAMP, s.company_id, s.office_id, s.company_name, s.office_name, s.name, s.name_kana, s.phone_number, s.email, s.version, s.state " +
            "FROM staffs s INNER JOIN @InsertedRows ir ON s.staff_id = ir.staff_id;" +
            "SELECT staff_id FROM @InsertedRows;";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, staff.getCompany_id());
                pstmt.setInt(2, staff.getOffice_id());
                pstmt.setString(3, staff.getCompany_name());
                pstmt.setString(4, staff.getOffice_name());
                pstmt.setString(5, staff.getName());
                pstmt.setString(6, staff.getName_kana());
                pstmt.setString(7, staff.getPhone_number());
                pstmt.setString(8, staff.getEmail());
                pstmt.setInt(9, staff.getVersion());
                pstmt.setInt(10, staff.getState());

                pstmt.setString(11, editor);

                boolean hasResultSet = pstmt.execute();

                if (hasResultSet) {
                    try (ResultSet rs = pstmt.getResultSet()) {
                        if (rs.next()) {
                            return rs.getInt("staff_id");
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

    // 更新
    public Integer updateStaff(StaffEntity staff, String editor) {
        String sql = 
            "DECLARE @UpdatedRows TABLE (staff_id INT);" +
            "UPDATE staffs SET company_id=?, office_id=?, company_name=?, office_name=?, name=?, name_kana=?, phone_number=?, email=?, version=?, state=? " +
            "OUTPUT INSERTED.staff_id INTO @UpdatedRows " +
            "WHERE staff_id=?;" +
            "INSERT INTO staffs_log (staff_id, editor, process, log_date, company_id, office_id, company_name, office_name, name, name_kana, phone_number, email, version, state) " +
            "SELECT s.staff_id, ?, 'UPDATE', CURRENT_TIMESTAMP, s.company_id, s.office_id, s.company_name, s.office_name, s.name, s.name_kana, s.phone_number, s.email, s.version, s.state " +
            "FROM staffs s INNER JOIN @UpdatedRows ur ON s.staff_id = ur.staff_id;" +
            "SELECT staff_id FROM @UpdatedRows;";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, staff.getCompany_id());
                pstmt.setInt(2, staff.getOffice_id());
                pstmt.setString(3, staff.getCompany_name());
                pstmt.setString(4, staff.getOffice_name());
                pstmt.setString(5, staff.getName());
                pstmt.setString(6, staff.getName_kana());
                pstmt.setString(7, staff.getPhone_number());
                pstmt.setString(8, staff.getEmail());
                pstmt.setInt(9, staff.getVersion());
                pstmt.setInt(10, staff.getState());

                pstmt.setInt(11, staff.getStaff_id());

                pstmt.setString(12, editor);

                boolean hasResultSet = pstmt.execute();

                if (hasResultSet) {
                    try (ResultSet rs = pstmt.getResultSet()) {
                        if (rs.next()) {
                            return rs.getInt("staff_id");
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

    // staff_idによる取得
    public StaffEntity findById(int staffId) {
        return genericRepository.findOne(
        "SELECT * FROM staffs WHERE staff_id = ? AND NOT (state = ?)",
            ps -> {
                ps.setInt(1, staffId); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
            },
            StaffEntity::new // Supplier<T>
        );
    }

    // 全件取得の例（必要に応じて）
    public List<StaffEntity> findAll() {
        return genericRepository.findAll(
            "SELECT * FROM staffs WHERE NOT (state = ?)",
            ps -> ps.setInt(1, Enums.state.DELETE.getCode()),
            StaffEntity::new
        );
    }
}

