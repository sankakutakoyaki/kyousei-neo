package com.kyouseipro.neo.query.parameter.corporation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
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

    public static void bindFindById(PreparedStatement ps, Integer officeId) throws SQLException {
        ps.setInt(1, officeId); // 1番目の ?
        ps.setInt(2, Enums.state.DELETE.getCode()); 
    }

    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
    }

    public static void bindFindAllClient(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, 0); // 1番目の ?
        ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
        ps.setInt(3, Enums.state.DELETE.getCode());     // 3番目の ?
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

