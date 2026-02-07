package com.kyouseipro.neo.corporation.office.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.corporation.office.entity.OfficeEntity;
import com.kyouseipro.neo.corporation.office.entity.OfficeEntityRequest;

public class OfficeParameterBinder {

    // public static int bindInsert(PreparedStatement pstmt, OfficeEntity office, String editor) throws SQLException {
    //     int index = 1;
    //     pstmt.setInt(index++, office.getCompanyId());
    //     pstmt.setString(index++, office.getName());
    //     pstmt.setString(index++, office.getNameKana());
    //     pstmt.setString(index++, office.getTelNumber());
    //     pstmt.setString(index++, office.getFaxNumber());
    //     pstmt.setString(index++, office.getPostalCode());
    //     pstmt.setString(index++, office.getFullAddress());
    //     pstmt.setString(index++, office.getEmail());
    //     pstmt.setString(index++, office.getWebAddress());
    //     pstmt.setInt(index++, office.getVersion());
    //     pstmt.setInt(index++, office.getState());

    //     pstmt.setString(index++, editor);
    //     return index;
    // }

    // public static int bindUpdate(PreparedStatement pstmt, OfficeEntity office, String editor) throws SQLException {
    //     int index = 1;
    //     pstmt.setInt(index++, office.getCompanyId());
    //     pstmt.setString(index++, office.getName());
    //     pstmt.setString(index++, office.getNameKana());
    //     pstmt.setString(index++, office.getTelNumber());
    //     pstmt.setString(index++, office.getFaxNumber());
    //     pstmt.setString(index++, office.getPostalCode());
    //     pstmt.setString(index++, office.getFullAddress());
    //     pstmt.setString(index++, office.getEmail());
    //     pstmt.setString(index++, office.getWebAddress());
    //     pstmt.setInt(index++, office.getVersion() +1);
    //     pstmt.setInt(index++, office.getState());

    //     pstmt.setInt(index++, office.getOfficeId());  // WHERE句
    //     pstmt.setInt(index++, office.getVersion());
        
    //     pstmt.setString(index++, editor);
    //     return index;
    // }
    private static int bindSaveParameter (PreparedStatement ps, OfficeEntityRequest req, String editor, int idx) throws SQLException {
        if (req.getCompanyId() != null) {
            ps.setInt(idx++, req.getCompanyId());
        }

        if (req.getName() != null) {
            ps.setString(idx++, req.getName());
        }
        if (req.getNameKana() != null) {
            ps.setString(idx++, req.getNameKana());
        }

        if (req.getTelNumber() != null) {
            ps.setString(idx++, req.getTelNumber());
        }
        if (req.getFaxNumber() != null) {
            ps.setString(idx++, req.getFaxNumber());
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
        if (req.getWebAddress() != null) {
            ps.setString(idx, req.getWebAddress());
        }
        
        return idx;
    }

    public static void bindBulkInsert(PreparedStatement ps, OfficeEntityRequest req, String editor) throws SQLException {

        int idx = bindSaveParameter(ps, req, editor, 1);

        // ログ用
        ps.setString(idx++, editor);          
    }

    public static void bindBulkUpdate(PreparedStatement ps, OfficeEntityRequest req, String editor) throws SQLException {

        int idx = bindSaveParameter(ps, req, editor, 1);

        // WHERE
        ps.setInt(idx++, req.getCompanyId());
        ps.setInt(idx++, req.getVersion());
        ps.setInt(idx++, Enums.state.DELETE.getCode());

        // ログ用
        ps.setString(idx++, editor);          
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
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        return index;
    }
}

