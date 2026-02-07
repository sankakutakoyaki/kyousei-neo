package com.kyouseipro.neo.corporation.staff.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.corporation.staff.entity.StaffEntity;
import com.kyouseipro.neo.corporation.staff.entity.StaffEntityRequest;

public class StaffParameterBinder {

    // public static int bindInsert(PreparedStatement pstmt, StaffEntity staff, String editor) throws SQLException {
    //     int index = 1;
    //     pstmt.setInt(index++, staff.getCompanyId());
    //     pstmt.setInt(index++, staff.getOfficeId());
    //     pstmt.setString(index++, staff.getName());
    //     pstmt.setString(index++, staff.getNameKana());
    //     pstmt.setString(index++, staff.getPhoneNumber());
    //     pstmt.setString(index++, staff.getEmail());
    //     pstmt.setInt(index++, staff.getVersion());
    //     pstmt.setInt(index++, staff.getState());

    //     pstmt.setString(index++, editor);
    //     return index;
    // }

    // public static int bindUpdate(PreparedStatement pstmt, StaffEntity staff, String editor) throws SQLException {
    //     int index = 1;
    //     pstmt.setInt(index++, staff.getCompanyId());
    //     pstmt.setInt(index++, staff.getOfficeId());
    //     pstmt.setString(index++, staff.getName());
    //     pstmt.setString(index++, staff.getNameKana());
    //     pstmt.setString(index++, staff.getPhoneNumber());
    //     pstmt.setString(index++, staff.getEmail());
    //     pstmt.setInt(index++, staff.getVersion());
    //     pstmt.setInt(index++, staff.getState() +1);

    //     pstmt.setInt(index++, staff.getStaffId());
    //     pstmt.setInt(index++, staff.getVersion());
        
    //     pstmt.setString(index++, editor);
    //     return index;
    // }
    private static int bindSaveParameter (PreparedStatement ps, StaffEntityRequest req, String editor, int idx) throws SQLException {
        if (req.getCompanyId() != null) {
            ps.setInt(idx++, req.getCompanyId());
        }
        if (req.getOfficeId() != null) {
            ps.setInt(idx++, req.getOfficeId());
        }

        if (req.getName() != null) {
            ps.setString(idx++, req.getName());
        }
        if (req.getNameKana() != null) {
            ps.setString(idx++, req.getNameKana());
        }

        if (req.getPhoneNumber() != null) {
            ps.setString(idx++, req.getPhoneNumber());
        }
        if (req.getEmail() != null) {
            ps.setString(idx++, req.getEmail());
        }
        
        return idx;
    }

    public static void bindBulkInsert(PreparedStatement ps, StaffEntityRequest req, String editor) throws SQLException {

        int idx = bindSaveParameter(ps, req, editor, 1);

        // ログ用
        ps.setString(idx++, editor);          
    }

    public static void bindBulkUpdate(PreparedStatement ps, StaffEntityRequest req, String editor) throws SQLException {

        int idx = bindSaveParameter(ps, req, editor, 1);

        // WHERE
        ps.setInt(idx++, req.getCompanyId());
        ps.setInt(idx++, req.getVersion());
        ps.setInt(idx++, Enums.state.DELETE.getCode());

        // ログ用
        ps.setString(idx++, editor);          
    }

    public static int bindFindById(PreparedStatement ps, Integer staffId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, staffId);
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
        ps.setString(index++, editor);
        return index;
    }

    public static int bindDownloadCsvByIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 2. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 3. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 4. AND NOT (state = ?)
        return index;
    }
}
