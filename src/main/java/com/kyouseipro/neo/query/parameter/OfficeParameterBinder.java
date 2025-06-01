package com.kyouseipro.neo.query.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.corporation.OfficeEntity;

public class OfficeParameterBinder {

    public static void bindInsertOfficeParameters(PreparedStatement pstmt, OfficeEntity office, String editor) throws SQLException {
        pstmt.setInt(1, office.getCompany_id());
        pstmt.setString(2, office.getCompany_name());
        pstmt.setString(3, office.getName());
        pstmt.setString(4, office.getName_kana());
        pstmt.setString(5, office.getTel_number());
        pstmt.setString(6, office.getFax_number());
        pstmt.setString(7, office.getPostal_code());
        pstmt.setString(8, office.getFull_address());
        pstmt.setString(9, office.getEmail());
        pstmt.setString(10, office.getWeb_address());
        pstmt.setInt(11, office.getVersion());
        pstmt.setInt(12, office.getState());

        pstmt.setString(13, editor);
    }

    public static void bindUpdateOfficeParameters(PreparedStatement pstmt, OfficeEntity office, String editor) throws SQLException {
        pstmt.setInt(1, office.getCompany_id());
        pstmt.setString(2, office.getCompany_name());
        pstmt.setString(3, office.getName());
        pstmt.setString(4, office.getName_kana());
        pstmt.setString(5, office.getTel_number());
        pstmt.setString(6, office.getFax_number());
        pstmt.setString(7, office.getPostal_code());
        pstmt.setString(8, office.getFull_address());
        pstmt.setString(9, office.getEmail());
        pstmt.setString(10, office.getWeb_address());
        pstmt.setInt(11, office.getVersion());
        pstmt.setInt(12, office.getState());

        pstmt.setInt(13, office.getOffice_id());  // WHERE句

        pstmt.setString(14, editor);
    }
}

