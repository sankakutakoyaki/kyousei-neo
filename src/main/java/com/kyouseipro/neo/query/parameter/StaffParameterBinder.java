package com.kyouseipro.neo.query.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.corporation.StaffEntity;

public class StaffParameterBinder {

    public static void bindInsertStaffParameters(PreparedStatement pstmt, StaffEntity staff, String editor) throws SQLException {
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
    }

    public static void bindUpdateStaffParameters(PreparedStatement pstmt, StaffEntity staff, String editor) throws SQLException {
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
    }
}
