package com.kyouseipro.neo.query.parameter.corporation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;

public class CompanyParameterBinder {

    public static int bindInsert(PreparedStatement pstmt, CompanyEntity company, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, company.getCategory());
        pstmt.setString(index++, company.getName());
        pstmt.setString(index++, company.getName_kana());
        pstmt.setString(index++, company.getTel_number());
        pstmt.setString(index++, company.getFax_number());
        pstmt.setString(index++, company.getPostal_code());
        pstmt.setString(index++, company.getFull_address());
        pstmt.setString(index++, company.getEmail());
        pstmt.setString(index++, company.getWeb_address());
        pstmt.setInt(index++, company.getIs_original_price());
        pstmt.setInt(index++, company.getVersion());
        pstmt.setInt(index++, company.getState());

        pstmt.setString(index++, editor);  // ログ用エディタ
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, CompanyEntity company, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, company.getCategory());
        pstmt.setString(index++, company.getName());
        pstmt.setString(index++, company.getName_kana());
        pstmt.setString(index++, company.getTel_number());
        pstmt.setString(index++, company.getFax_number());
        pstmt.setString(index++, company.getPostal_code());
        pstmt.setString(index++, company.getFull_address());
        pstmt.setString(index++, company.getEmail());
        pstmt.setString(index++, company.getWeb_address());
        pstmt.setInt(index++, company.getIs_original_price());
        pstmt.setInt(index++, company.getVersion());
        pstmt.setInt(index++, company.getState());

        pstmt.setInt(index++, company.getCompany_id());  // WHERE句用ID

        pstmt.setString(index++, editor);  // ログ用エディタ
        return index;
    }

    public static int bindFindById(PreparedStatement ps, Integer companyId) throws SQLException {
        int index = 1;
        ps.setInt(index++, companyId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindAllClient(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, 0);
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
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        return index;
    }
}
