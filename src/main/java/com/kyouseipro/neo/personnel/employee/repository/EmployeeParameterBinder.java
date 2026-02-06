package com.kyouseipro.neo.personnel.employee.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntityRequest;

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

    // public static int bindUpdate(PreparedStatement pstmt, EmployeeEntity e, String editor) throws SQLException {
    //     int index = 1;
    //     pstmt.setInt(index++, e.getCompanyId());
    //     pstmt.setInt(index++, e.getOfficeId());
    //     pstmt.setString(index++, e.getAccount());
    //     pstmt.setInt(index++, e.getCode());
    //     pstmt.setInt(index++, e.getCategory());
    //     pstmt.setString(index++, e.getLastName());
    //     pstmt.setString(index++, e.getFirstName());
    //     pstmt.setString(index++, e.getLastNameKana());
    //     pstmt.setString(index++, e.getFirstNameKana());
    //     pstmt.setString(index++, e.getPhoneNumber());
    //     pstmt.setString(index++, e.getPostalCode());
    //     pstmt.setString(index++, e.getFullAddress());
    //     pstmt.setString(index++, e.getEmail());
    //     pstmt.setInt(index++, e.getGender());
    //     pstmt.setInt(index++, e.getBloodType());

    //     if (e.getBirthday() != null) {
    //         pstmt.setDate(index++, java.sql.Date.valueOf(e.getBirthday()));
    //     } else {
    //         pstmt.setNull(index++, java.sql.Types.DATE);
    //     }

    //     pstmt.setString(index++, e.getEmergencyContact());
    //     pstmt.setString(index++, e.getEmergencyContactNumber());

    //     if (e.getDateOfHire() != null) {
    //         pstmt.setDate(index++, java.sql.Date.valueOf(e.getDateOfHire()));
    //     } else {
    //         pstmt.setNull(index++, java.sql.Types.DATE);
    //     }

    //     pstmt.setInt(index++, e.getVersion() +1);
    //     pstmt.setInt(index++, e.getState());

    //     pstmt.setInt(index++, e.getEmployeeId()); // WHERE句
    //     pstmt.setInt(index++, e.getVersion());
    //     pstmt.setString(index++, editor);          // ログ用
    //     return index;
    // }

    public static int bindUpdateCode(PreparedStatement pstmt, int id, int code, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, code);

        pstmt.setInt(index++, id); // WHERE句
        pstmt.setString(index++, editor);          // ログ用
        return index;
    }

    private static int bindSaveParameter (PreparedStatement ps, EmployeeEntityRequest req, String editor, int idx) throws SQLException {
        if (req.getAccount() != null) {
            ps.setString(idx++, req.getAccount());
        }
        if (req.getCode() != null) {
            ps.setInt(idx++, req.getCode());
        }

        if (req.getCompanyId() != null) {
            ps.setInt(idx++, req.getCompanyId());
        }
        if (req.getOfficeId() != null) {
            ps.setInt(idx++, req.getOfficeId());
        }
        if (req.getCategory() != null) {
            ps.setInt(idx++, req.getCategory());
        }
        if (req.getGender() != null) {
            ps.setInt(idx++, req.getGender());
        }
        if (req.getBloodType() != null) {
            ps.setInt(idx++, req.getBloodType());
        }

        if (req.getPhoneNumber() != null) {
            ps.setString(idx++, req.getPhoneNumber());
        }
        if (req.getPostalCode() != null) {
            ps.setString(idx++, req.getPostalCode());
        }
        if (req.getFullAddress() != null) {
            ps.setString(idx++, req.getFullAddress());
        }
        if (req.getEmail() != null) {
            ps.setString(idx++, req.getEmail());
        }

        if (req.getBirthday() != null) {
            ps.setDate(idx++, Date.valueOf(req.getBirthday()));
        }
        if (req.getDateOfHire() != null) {
            ps.setDate(idx++, Date.valueOf(req.getDateOfHire()));
        }
        
        return idx;
    }

    public static void bindBulkInsert(PreparedStatement ps, EmployeeEntityRequest req, String editor) throws SQLException {

        int idx = bindSaveParameter(ps, req, editor, 1);

        // WHERE
        ps.setInt(idx++, req.getEmployeeId());
        ps.setInt(idx++, req.getVersion());
        ps.setInt(idx++, Enums.state.DELETE.getCode());

        // ログ用
        ps.setString(idx++, editor);          
    }

    public static void bindBulkUpdate(PreparedStatement ps, EmployeeEntityRequest req, String editor) throws SQLException {

        int idx = bindSaveParameter(ps, req, editor, 1);
        // ログ用
        ps.setString(idx++, editor);          
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
