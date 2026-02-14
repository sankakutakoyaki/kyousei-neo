package com.kyouseipro.neo.personnel.employee.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntityRequest;

public class EmployeeParameterBinder {
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

        if (req.getLastName() != null) {
            ps.setString(idx++, req.getLastName());
        }
        if (req.getFirstName() != null) {
            ps.setString(idx++, req.getFirstName());
        }
        if (req.getLastNameKana() != null) {
            ps.setString(idx++, req.getLastNameKana());
        }
        if (req.getFirstNameKana() != null) {
            ps.setString(idx++, req.getFirstNameKana());
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

        // ログ用
        ps.setString(idx++, editor);          
    }

    public static void bindBulkUpdate(PreparedStatement ps, EmployeeEntityRequest req, String editor) throws SQLException {

        int idx = bindSaveParameter(ps, req, editor, 1);

        // WHERE
        ps.setInt(idx++, req.getEmployeeId());
        ps.setInt(idx++, req.getVersion());
        ps.setInt(idx++, Enums.state.DELETE.getCode());

        // ログ用
        ps.setString(idx++, editor);          
    }

    public static int bindUpdateCode(PreparedStatement pstmt, int id, int code, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, code);

        pstmt.setInt(index++, id); 
        pstmt.setString(index++, editor); 
        return index;
    }

    public static int bindUpdatePhone(PreparedStatement pstmt, int id, String phone, String editor) throws SQLException {
        int index = 1;
        pstmt.setString(index++, phone);

        pstmt.setInt(index++, id);
        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindDeleteByIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index, editor);
        return index;
    }

    public static int bindDownloadCsvByIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index, Enums.state.DELETE.getCode());
        return index;
    }
}
