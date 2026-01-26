package com.kyouseipro.neo.query.parameter.personnel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;

public class EmployeeParameterBinder {
    public static int bindInsert(PreparedStatement pstmt, EmployeeEntity e, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, e.getCompanyId());
        pstmt.setInt(index++, e.getOfficeId());
        pstmt.setString(index++, e.getAccount());
        pstmt.setInt(index++, e.getCode());
        pstmt.setInt(index++, e.getCategory());
        pstmt.setString(index++, e.getLastName());
        pstmt.setString(index++, e.getFirstName());
        pstmt.setString(index++, e.getLastNameKana());
        pstmt.setString(index++, e.getFirstNameKana());
        pstmt.setString(index++, e.getPhoneNumber());
        pstmt.setString(index++, e.getPostalCode());
        pstmt.setString(index++, e.getFullAddress());
        pstmt.setString(index++, e.getEmail());
        pstmt.setInt(index++, e.getGender());
        pstmt.setInt(index++, e.getBloodType());

        if (e.getBirthday() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(e.getBirthday()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }

        pstmt.setString(index++, e.getEmergencyContact());
        pstmt.setString(index++, e.getEmergencyContactNumber());

        if (e.getDateOfHire() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(e.getDateOfHire()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }

        pstmt.setInt(index++, e.getVersion());
        pstmt.setInt(index++, e.getState());

        pstmt.setString(index++, editor);  // editor for INSERT INTO employees_log
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, EmployeeEntity e, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, e.getCompanyId());
        pstmt.setInt(index++, e.getOfficeId());
        pstmt.setString(index++, e.getAccount());
        pstmt.setInt(index++, e.getCode());
        pstmt.setInt(index++, e.getCategory());
        pstmt.setString(index++, e.getLastName());
        pstmt.setString(index++, e.getFirstName());
        pstmt.setString(index++, e.getLastNameKana());
        pstmt.setString(index++, e.getFirstNameKana());
        pstmt.setString(index++, e.getPhoneNumber());
        pstmt.setString(index++, e.getPostalCode());
        pstmt.setString(index++, e.getFullAddress());
        pstmt.setString(index++, e.getEmail());
        pstmt.setInt(index++, e.getGender());
        pstmt.setInt(index++, e.getBloodType());

        if (e.getBirthday() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(e.getBirthday()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }

        pstmt.setString(index++, e.getEmergencyContact());
        pstmt.setString(index++, e.getEmergencyContactNumber());

        if (e.getDateOfHire() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(e.getDateOfHire()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }

        pstmt.setInt(index++, e.getVersion() +1);
        pstmt.setInt(index++, e.getState());

        pstmt.setInt(index++, e.getEmployeeId()); // WHERE句
        pstmt.setInt(index++, e.getVersion());
        pstmt.setString(index++, editor);          // ログ用
        return index;
    }

    public static int bindUpdateCode(PreparedStatement pstmt, int id, int code, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, code);

        pstmt.setInt(index++, id); // WHERE句
        pstmt.setString(index++, editor);          // ログ用
        return index;
    }

    public static int bindUpdatePhone(PreparedStatement pstmt, int id, String phone, String editor) throws SQLException {
        int index = 1;
        pstmt.setString(index++, phone);

        pstmt.setInt(index++, id); // WHERE句
        pstmt.setString(index++, editor);          // ログ用
        return index;
    }

    public static int bindFindById(PreparedStatement ps, Integer id) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindByCode(PreparedStatement ps, Integer code) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, code);
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

    public static int bindDeleteByIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index, editor); // 4. log
        return index;
    }

    public static int bindDownloadCsvByIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        return index;
    }
}
