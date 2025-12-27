package com.kyouseipro.neo.query.parameter.recycle;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.recycle.RecycleDateEntity;
import com.kyouseipro.neo.entity.recycle.RecycleEntity;

public class RecycleParameterBinder {
    public static int bindInsertRecycleParameters(PreparedStatement pstmt, RecycleEntity r, String editor, int index) throws SQLException {
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
        return index;
    }

    public static int bindUpdateRecycleParameters(PreparedStatement pstmt, RecycleEntity r, String editor, int index) throws SQLException {
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
        pstmt.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));

        pstmt.setInt(index++, r.getRecycle_id());
        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindFindById(PreparedStatement ps, Integer recycleId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, recycleId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindFindByNumber(PreparedStatement ps, String number) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, number);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }


    public static int bindExistsByNumber(PreparedStatement ps, String number) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, number);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindUpdateRecycleDateParameters(PreparedStatement ps, int id, LocalDate date, String editor, int index) throws SQLException {
        ps.setDate(index++, java.sql.Date.valueOf(date));
        ps.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));
        ps.setInt(index++, id);
        ps.setString(index, editor);
        return index;
    }

    public static int bindInsertRecycleDateParameters(PreparedStatement ps, RecycleDateEntity r, String editor, int index) throws SQLException {
        ps.setString(index++, r.getRecycle_number());
        ps.setString(index++, r.getMolding_number());
        ps.setDate(index++, java.sql.Date.valueOf(r.getDate()));
        ps.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(index, editor);
        return index;
    }

    public static int bindDeleteRecycleParameters(PreparedStatement ps, int id, String editor, int index) throws SQLException {
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setString(index, editor);
        return index;
    }

    public static int bindFindByBetweenRecycleEntity(PreparedStatement ps, LocalDate start, LocalDate end) throws SQLException {
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
        return index;
    }

    public static int bindSaveRecycleListParameters(PreparedStatement pstmt, List<RecycleEntity> list, String editor) throws SQLException {
        int index = 1;
        for (RecycleEntity entity : list) {
            if (entity.getRecycle_id() > 0){
                index = RecycleParameterBinder.bindUpdateRecycleParameters(pstmt, entity, editor, index);
                // index = index + 16;
            } else {
                index = RecycleParameterBinder.bindInsertRecycleParameters(pstmt, entity, editor, index);
                // index = index + 15;
            }
        }
        return index;
    }
    
    public static int bindUpdateRecycleDateListParameters(PreparedStatement pstmt, List<RecycleDateEntity> list, String editor) throws SQLException {
        int index = 1;
        for (RecycleDateEntity entity : list) {
            if (entity.getRecycle_id() > 0){
                index = RecycleParameterBinder.bindUpdateRecycleDateParameters(pstmt, entity.getRecycle_id(), entity.getDate(), editor, index);
                // index = index + 3;
            } else {
                index = RecycleParameterBinder.bindInsertRecycleDateParameters(pstmt, entity, editor, index);
                // index = index + 6;
            }
        }
        return index;
    }

    public static int bindDeleteForIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index, editor); // 4. log
        return index;
    }

    public static int bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
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
        return index;
    }
}
