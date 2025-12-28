package com.kyouseipro.neo.query.parameter.corporation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;

public class OfficeParameterBinder {

    public static int bindInsert(PreparedStatement pstmt, OfficeEntity office, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, office.getCompany_id());
        pstmt.setString(index++, office.getName());
        pstmt.setString(index++, office.getName_kana());
        pstmt.setString(index++, office.getTel_number());
        pstmt.setString(index++, office.getFax_number());
        pstmt.setString(index++, office.getPostal_code());
        pstmt.setString(index++, office.getFull_address());
        pstmt.setString(index++, office.getEmail());
        pstmt.setString(index++, office.getWeb_address());
        pstmt.setInt(index++, office.getVersion());
        pstmt.setInt(index++, office.getState());

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, OfficeEntity office, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, office.getCompany_id());
        pstmt.setString(index++, office.getName());
        pstmt.setString(index++, office.getName_kana());
        pstmt.setString(index++, office.getTel_number());
        pstmt.setString(index++, office.getFax_number());
        pstmt.setString(index++, office.getPostal_code());
        pstmt.setString(index++, office.getFull_address());
        pstmt.setString(index++, office.getEmail());
        pstmt.setString(index++, office.getWeb_address());
        pstmt.setInt(index++, office.getVersion() +1);
        pstmt.setInt(index++, office.getState());

        pstmt.setInt(index++, office.getOffice_id());  // WHERE句
        pstmt.setInt(index++, office.getVersion());
        
        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindFindById(PreparedStatement ps, Integer officeId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, officeId);
        return index;
    }

    public static int bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindAllClient(PreparedStatement ps, Void unused) throws SQLException {     
        int index = 1;   
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, 0);
        return index;
    }

    public static int bindDeleteForIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index++, editor);
        return index;
    }

    public static int bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        return index;
    }
}

