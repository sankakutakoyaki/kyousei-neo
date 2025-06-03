package com.kyouseipro.neo.query.parameter.personnel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;

public class EmployeeParameterBinder {
    public static void bindInsertEmployeeParameters(PreparedStatement pstmt, EmployeeEntity e, String editor) throws SQLException {
        pstmt.setInt(1, e.getCompany_id());
        pstmt.setInt(2, e.getOffice_id());
        pstmt.setString(3, e.getAccount());
        pstmt.setInt(4, e.getCode());
        pstmt.setInt(5, e.getCategory());
        pstmt.setString(6, e.getLast_name());
        pstmt.setString(7, e.getFirst_name());
        pstmt.setString(8, e.getFull_name());
        pstmt.setString(9, e.getLast_name_kana());
        pstmt.setString(10, e.getFirst_name_kana());
        pstmt.setString(11, e.getFull_name_kana());
        pstmt.setString(12, e.getPhone_number());
        pstmt.setString(13, e.getPostal_code());
        pstmt.setString(14, e.getFull_address());
        pstmt.setString(15, e.getEmail());
        pstmt.setInt(16, e.getGender());
        pstmt.setInt(17, e.getBlood_type());

        if (e.getBirthday() != null) {
            pstmt.setDate(18, java.sql.Date.valueOf(e.getBirthday()));
        } else {
            pstmt.setNull(18, java.sql.Types.DATE);
        }

        pstmt.setString(19, e.getEmergency_contact());
        pstmt.setString(20, e.getEmergency_contact_number());

        if (e.getDate_of_hire() != null) {
            pstmt.setDate(21, java.sql.Date.valueOf(e.getDate_of_hire()));
        } else {
            pstmt.setNull(21, java.sql.Types.DATE);
        }

        pstmt.setInt(22, e.getVersion());
        pstmt.setInt(23, e.getState());

        pstmt.setString(24, editor);  // editor for INSERT INTO employees_log
    }

    public static void bindUpdateEmployeeParameters(PreparedStatement pstmt, EmployeeEntity e, String editor) throws SQLException {
        pstmt.setInt(1, e.getCompany_id());
        pstmt.setInt(2, e.getOffice_id());
        pstmt.setString(3, e.getAccount());
        pstmt.setInt(4, e.getCode());
        pstmt.setInt(5, e.getCategory());
        pstmt.setString(6, e.getLast_name());
        pstmt.setString(7, e.getFirst_name());
        pstmt.setString(8, e.getFull_name());
        pstmt.setString(9, e.getLast_name_kana());
        pstmt.setString(10, e.getFirst_name_kana());
        pstmt.setString(11, e.getFull_name_kana());
        pstmt.setString(12, e.getPhone_number());
        pstmt.setString(13, e.getPostal_code());
        pstmt.setString(14, e.getFull_address());
        pstmt.setString(15, e.getEmail());
        pstmt.setInt(16, e.getGender());
        pstmt.setInt(17, e.getBlood_type());

        if (e.getBirthday() != null) {
            pstmt.setDate(18, java.sql.Date.valueOf(e.getBirthday()));
        } else {
            pstmt.setNull(18, java.sql.Types.DATE);
        }

        pstmt.setString(19, e.getEmergency_contact());
        pstmt.setString(20, e.getEmergency_contact_number());

        if (e.getDate_of_hire() != null) {
            pstmt.setDate(21, java.sql.Date.valueOf(e.getDate_of_hire()));
        } else {
            pstmt.setNull(21, java.sql.Types.DATE);
        }

        pstmt.setInt(22, e.getVersion());
        pstmt.setInt(23, e.getState());

        pstmt.setInt(24, e.getEmployee_id()); // WHERE句
        pstmt.setString(25, editor);          // ログ用
    }

    public static void bindFindById(PreparedStatement ps, Integer employeeId) throws SQLException {
        ps.setInt(1, employeeId);
        ps.setInt(2, Enums.state.DELETE.getCode());
    }

    public static void bindFindByAccount(PreparedStatement ps, String account) throws SQLException {
        ps.setString(1, account);
        ps.setInt(2, Enums.state.DELETE.getCode());
    }

    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
    }

    public static void bindDeleteForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
    }

    public static void bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
    }
}
