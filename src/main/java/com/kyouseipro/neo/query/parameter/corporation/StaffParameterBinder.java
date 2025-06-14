package com.kyouseipro.neo.query.parameter.corporation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.StaffEntity;

public class StaffParameterBinder {

    public static void bindInsertStaffParameters(PreparedStatement pstmt, StaffEntity staff, String editor) throws SQLException {
        pstmt.setInt(1, staff.getCompany_id());
        pstmt.setInt(2, staff.getOffice_id());
        pstmt.setString(3, staff.getName());
        pstmt.setString(4, staff.getName_kana());
        pstmt.setString(5, staff.getPhone_number());
        pstmt.setString(6, staff.getEmail());
        pstmt.setInt(7, staff.getVersion());
        pstmt.setInt(8, staff.getState());

        pstmt.setString(9, editor);
    }

    public static void bindUpdateStaffParameters(PreparedStatement pstmt, StaffEntity staff, String editor) throws SQLException {
        pstmt.setInt(1, staff.getCompany_id());
        pstmt.setInt(2, staff.getOffice_id());
        pstmt.setString(3, staff.getName());
        pstmt.setString(4, staff.getName_kana());
        pstmt.setString(5, staff.getPhone_number());
        pstmt.setString(6, staff.getEmail());
        pstmt.setInt(7, staff.getVersion());
        pstmt.setInt(8, staff.getState());

        pstmt.setInt(9, staff.getStaff_id());

        pstmt.setString(10, editor);
    }

    public static void bindFindById(PreparedStatement ps, Integer staffId) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
        ps.setInt(4, staffId);
        
    }

    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
    }

    public static void bindDeleteForIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index, editor);
    }

    public static void bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 2. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 3. company_id IN (?, ?, ?)
        }
        ps.setInt(index, Enums.state.DELETE.getCode()); // 4. AND NOT (state = ?)
    }
}
