package com.kyouseipro.neo.query.parameter.recycle;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.recycle.RecycleEntity;

public class RecycleParameterBinder {
    public static void bindInsertRecycleParameters(PreparedStatement pstmt, RecycleEntity r, String editor, int index) throws SQLException {
        pstmt.setString(index++, r.getRecycle_number());
        pstmt.setString(index++, r.getMolding_number());
        pstmt.setInt(index++, r.getMaker_id());
        pstmt.setInt(index++, r.getItem_id());
        if (r.getUse_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(r.getUse_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        if (r.getDelivery_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(r.getDelivery_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        if (r.getShipping_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(r.getShipping_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        if (r.getLoss_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(r.getLoss_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        pstmt.setInt(index++, r.getCompany_id());
        pstmt.setInt(index++, r.getOffice_id());
        pstmt.setInt(index++, r.getRecycling_fee());
        pstmt.setInt(index++, r.getDisposal_site_id());

        pstmt.setInt(index++, r.getVersion());
        pstmt.setInt(index++, r.getState());

        pstmt.setString(index++, editor);
    }

    public static void bindUpdateRecycleParameters(PreparedStatement pstmt, RecycleEntity r, String editor, int index) throws SQLException {
        pstmt.setString(index++, r.getRecycle_number());
        pstmt.setString(index++, r.getMolding_number());
        pstmt.setInt(index++, r.getMaker_id());
        pstmt.setInt(index++, r.getItem_id());
        if (r.getUse_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(r.getUse_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        if (r.getDelivery_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(r.getDelivery_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        if (r.getShipping_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(r.getShipping_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        if (r.getLoss_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(r.getLoss_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        pstmt.setInt(index++, r.getCompany_id());
        pstmt.setInt(index++, r.getOffice_id());
        pstmt.setInt(index++, r.getRecycling_fee());
        pstmt.setInt(index++, r.getDisposal_site_id());

        pstmt.setInt(index++, r.getVersion());
        pstmt.setInt(index++, r.getState());

        pstmt.setInt(index++, r.getRecycle_id());
        pstmt.setString(index++, editor);
    }

    public static void bindFindById(PreparedStatement ps, Integer recycleId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, recycleId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
    }

    public static void bindFindByNumber(PreparedStatement ps, String number) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, number);
        ps.setInt(index++, Enums.state.DELETE.getCode());
    }


    public static void bindExistsByNumber(PreparedStatement ps, String number) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, number);
        ps.setInt(index++, Enums.state.DELETE.getCode());
    }

    public static void bindDeleteRecycleParameters(PreparedStatement ps, int id, String editor, int index) throws SQLException {
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setString(index, editor);
    }

    public static void bindFindByBetweenRecycleEntity(PreparedStatement ps, LocalDate start, LocalDate end) throws SQLException {
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to   = end.plusDays(1).atStartOfDay();
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());

        ps.setTimestamp(index++, Timestamp.valueOf(from));
        ps.setTimestamp(index++, Timestamp.valueOf(to));

        // ps.setString(index++, start.toString());
        // ps.setString(index++, end.toString());
        // ps.setString(index++, start.toString());
        // ps.setString(index++, end.toString());
    }

    public static void bindSaveRecycleListParameters(PreparedStatement pstmt, List<RecycleEntity> list, String editor) throws SQLException {
        int index = 1;
        for (RecycleEntity entity : list) {
            if (entity.getRecycle_id() > 0){
                RecycleParameterBinder.bindUpdateRecycleParameters(pstmt, entity, editor, index);
                index = index + 16;
            } else {
                RecycleParameterBinder.bindInsertRecycleParameters(pstmt, entity, editor, index);
                index = index + 15;
            }
        }
    }

    public static void bindDeleteForIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index, editor); // 4. log
    }

    public static void bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id); // id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
    }
}
