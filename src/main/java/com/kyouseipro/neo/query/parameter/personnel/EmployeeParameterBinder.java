package com.kyouseipro.neo.query.parameter.personnel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;

public class EmployeeParameterBinder {
    public static int bindInsertEmployeeParameters(PreparedStatement pstmt, EmployeeEntity e, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, e.getCompany_id());
        pstmt.setInt(index++, e.getOffice_id());
        pstmt.setString(index++, e.getAccount());
        pstmt.setInt(index++, e.getCode());
        pstmt.setInt(index++, e.getCategory());
        pstmt.setString(index++, e.getLast_name());
        pstmt.setString(index++, e.getFirst_name());
        pstmt.setString(index++, e.getLast_name_kana());
        pstmt.setString(index++, e.getFirst_name_kana());
        pstmt.setString(index++, e.getPhone_number());
        pstmt.setString(index++, e.getPostal_code());
        pstmt.setString(index++, e.getFull_address());
        pstmt.setString(index++, e.getEmail());
        pstmt.setInt(index++, e.getGender());
        pstmt.setInt(index++, e.getBlood_type());

        if (e.getBirthday() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(e.getBirthday()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }

        pstmt.setString(index++, e.getEmergency_contact());
        pstmt.setString(index++, e.getEmergency_contact_number());

        if (e.getDate_of_hire() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(e.getDate_of_hire()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }

        pstmt.setInt(index++, e.getVersion());
        pstmt.setInt(index++, e.getState());

        pstmt.setString(index++, editor);  // editor for INSERT INTO employees_log
        return index;
    }

    public static int bindUpdateEmployeeParameters(PreparedStatement pstmt, EmployeeEntity e, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, e.getCompany_id());
        pstmt.setInt(index++, e.getOffice_id());
        pstmt.setString(index++, e.getAccount());
        pstmt.setInt(index++, e.getCode());
        pstmt.setInt(index++, e.getCategory());
        pstmt.setString(index++, e.getLast_name());
        pstmt.setString(index++, e.getFirst_name());
        pstmt.setString(index++, e.getLast_name_kana());
        pstmt.setString(index++, e.getFirst_name_kana());
        pstmt.setString(index++, e.getPhone_number());
        pstmt.setString(index++, e.getPostal_code());
        pstmt.setString(index++, e.getFull_address());
        pstmt.setString(index++, e.getEmail());
        pstmt.setInt(index++, e.getGender());
        pstmt.setInt(index++, e.getBlood_type());

        if (e.getBirthday() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(e.getBirthday()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }

        pstmt.setString(index++, e.getEmergency_contact());
        pstmt.setString(index++, e.getEmergency_contact_number());

        if (e.getDate_of_hire() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(e.getDate_of_hire()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }

        pstmt.setInt(index++, e.getVersion());
        pstmt.setInt(index++, e.getState());

        pstmt.setInt(index++, e.getEmployee_id()); // WHERE句
        pstmt.setString(index++, editor);          // ログ用
        return index;
    }

    public static int bindFindById(PreparedStatement ps, Integer employeeId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, employeeId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindByAccount(PreparedStatement ps, String account) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, account);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindDeleteForIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index, editor); // 4. log
        return index;
    }

    public static int bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        return index;
    }
}
