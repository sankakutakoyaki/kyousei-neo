package com.kyouseipro.neo.query.parameter.corporation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;

public class CompanyParameterBinder {

    public static void bindInsertCompanyParameters(PreparedStatement pstmt, CompanyEntity company, String editor) throws SQLException {
        pstmt.setInt(1, company.getCategory());
        pstmt.setString(2, company.getName());
        pstmt.setString(3, company.getName_kana());
        pstmt.setString(4, company.getTel_number());
        pstmt.setString(5, company.getFax_number());
        pstmt.setString(6, company.getPostal_code());
        pstmt.setString(7, company.getFull_address());
        pstmt.setString(8, company.getEmail());
        pstmt.setString(9, company.getWeb_address());
        pstmt.setInt(10, company.getVersion());
        pstmt.setInt(11, company.getState());

        pstmt.setString(12, editor);  // ログ用エディタ
    }

    public static void bindUpdateCompanyParameters(PreparedStatement pstmt, CompanyEntity company, String editor) throws SQLException {
        pstmt.setInt(1, company.getCategory());
        pstmt.setString(2, company.getName());
        pstmt.setString(3, company.getName_kana());
        pstmt.setString(4, company.getTel_number());
        pstmt.setString(5, company.getFax_number());
        pstmt.setString(6, company.getPostal_code());
        pstmt.setString(7, company.getFull_address());
        pstmt.setString(8, company.getEmail());
        pstmt.setString(9, company.getWeb_address());
        pstmt.setInt(10, company.getVersion());
        pstmt.setInt(11, company.getState());

        pstmt.setInt(12, company.getCompany_id());  // WHERE句用ID

        pstmt.setString(13, editor);  // ログ用エディタ
    }

    public static void bindFindById(PreparedStatement ps, Integer companyId) throws SQLException {
        ps.setInt(1, companyId);
        ps.setInt(2, Enums.state.DELETE.getCode());
    }

    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
    }

    public static void bindFindAllClient(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, 0);
        ps.setInt(2, Enums.state.DELETE.getCode());
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
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
    }
}
