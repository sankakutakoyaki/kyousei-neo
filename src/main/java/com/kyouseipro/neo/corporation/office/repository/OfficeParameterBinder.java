package com.kyouseipro.neo.corporation.office.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.corporation.office.entity.OfficeEntityRequest;

public class OfficeParameterBinder {
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

    public static int bindDeleteByIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, editor);
        return index;
    }

    public static int bindDownloadCsvByIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }
}

