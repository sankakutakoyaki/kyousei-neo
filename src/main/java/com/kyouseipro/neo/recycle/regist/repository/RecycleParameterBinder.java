package com.kyouseipro.neo.recycle.regist.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.dto.sql.common.SqlUtil;
import com.kyouseipro.neo.recycle.regist.entity.RecycleDateEntity;
import com.kyouseipro.neo.recycle.regist.entity.RecycleEntity;
import com.kyouseipro.neo.recycle.regist.entity.RecycleEntityRequest;

public class RecycleParameterBinder {
    public static int bindInsert(PreparedStatement pstmt, RecycleEntity r, String editor, int index) throws SQLException {
        pstmt.setString(index++, r.getRecycleNumber());
        pstmt.setString(index++, r.getMoldingNumber());
        pstmt.setInt(index++, r.getMakerId());
        pstmt.setInt(index++, r.getItemId());
        SqlUtil.setDateOrMax(pstmt, index++, r.getUseDate());
        SqlUtil.setDateOrMax(pstmt, index++, r.getDeliveryDate());
        SqlUtil.setDateOrMax(pstmt, index++, r.getShippingDate());
        SqlUtil.setDateOrMax(pstmt, index++, r.getLossDate());
        // if (r.getUseDate() != null) {
        //     pstmt.setDate(index++, java.sql.Date.valueOf(r.getUseDate()));
        // } else {
        //     pstmt.setNull(index++, java.sql.Types.DATE);
        // }
        // if (r.getDeliveryDate() != null) {
        //     pstmt.setDate(index++, java.sql.Date.valueOf(r.getDeliveryDate()));
        // } else {
        //     pstmt.setNull(index++, java.sql.Types.DATE);
        // }
        // if (r.getShippingDate() != null) {
        //     pstmt.setDate(index++, java.sql.Date.valueOf(r.getShippingDate()));
        // } else {
        //     pstmt.setNull(index++, java.sql.Types.DATE);
        // }
        // if (r.getLossDate() != null) {
        //     pstmt.setDate(index++, java.sql.Date.valueOf(r.getLossDate()));
        // } else {
        //     pstmt.setNull(index++, java.sql.Types.DATE);
        // }
        pstmt.setInt(index++, r.getCompanyId());
        pstmt.setInt(index++, r.getOfficeId());
        pstmt.setInt(index++, r.getRecyclingFee());
        pstmt.setInt(index++, r.getDisposalSiteId());
        pstmt.setInt(index++, r.getSlipNumber());

        pstmt.setInt(index++, r.getVersion());
        pstmt.setInt(index++, r.getState());

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, RecycleEntity r, String type, String editor, int index) throws SQLException {

        switch (type) {
            case "delivery":
                pstmt.setDate(index++, java.sql.Date.valueOf(r.getDeliveryDate()));
                pstmt.setInt(index++, r.getDisposalSiteId());
                break;
            case "shipping":
                pstmt.setDate(index++, java.sql.Date.valueOf(r.getShippingDate()));
                break;
            case "loss":
                pstmt.setDate(index++, java.sql.Date.valueOf(r.getLossDate()));
                break;
            case "edit":
                pstmt.setString(index++, r.getRecycleNumber());
                pstmt.setString(index++, r.getMoldingNumber());
                pstmt.setInt(index++, r.getMakerId());
                pstmt.setInt(index++, r.getItemId());
                SqlUtil.setDateOrMax(pstmt, index++, r.getUseDate());
                SqlUtil.setDateOrMax(pstmt, index++, r.getDeliveryDate());
                SqlUtil.setDateOrMax(pstmt, index++, r.getShippingDate());
                SqlUtil.setDateOrMax(pstmt, index++, r.getLossDate());
                // if (r.getUseDate() != null) {
                //     pstmt.setDate(index++, java.sql.Date.valueOf(r.getUseDate()));
                // } else {
                //     pstmt.setNull(index++, java.sql.Types.DATE);
                // }
                // if (r.getDeliveryDate() != null) {
                //     pstmt.setDate(index++, java.sql.Date.valueOf(r.getDeliveryDate()));
                // } else {
                //     pstmt.setNull(index++, java.sql.Types.DATE);
                // }
                // if (r.getShippingDate() != null) {
                //     pstmt.setDate(index++, java.sql.Date.valueOf(r.getShippingDate()));
                // } else {
                //     pstmt.setNull(index++, java.sql.Types.DATE);
                // }
                // if (r.getLossDate() != null) {
                //     pstmt.setDate(index++, java.sql.Date.valueOf(r.getLossDate()));
                // } else {
                //     pstmt.setNull(index++, java.sql.Types.DATE);
                // }
                pstmt.setInt(index++, r.getCompanyId());
                pstmt.setInt(index++, r.getOfficeId());
                pstmt.setInt(index++, r.getRecyclingFee());
                pstmt.setInt(index++, r.getDisposalSiteId());
                pstmt.setInt(index++, r.getSlipNumber());
                break;
            default:
                break;
        }

        pstmt.setInt(index++, r.getVersion() +1);
        pstmt.setInt(index++, r.getState());
        pstmt.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));

        pstmt.setString(index++, r.getRecycleNumber());
        // pstmt.setInt(index++, r.getRecycleId());
        // pstmt.setInt(index++, r.getVersion());
        pstmt.setInt(index++, Enums.state.DELETE.getCode());
        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindBulkUpdate(PreparedStatement ps, RecycleEntityRequest req, int idx) throws SQLException {
        if (req.getUseDate() != null) {
            ps.setDate(idx++, Date.valueOf(req.getUseDate()));
        }
        if (req.getDeliveryDate() != null) {
            ps.setDate(idx++, Date.valueOf(req.getDeliveryDate()));
        }
        if (req.getShippingDate() != null) {
            ps.setDate(idx++, Date.valueOf(req.getShippingDate()));
        }
        if (req.getLossDate() != null) {
            ps.setDate(idx++, Date.valueOf(req.getLossDate()));
        }
        if (req.getMakerId() != null) {
            ps.setInt(idx++, req.getMakerId());
        }
        if (req.getItemId() != null) {
            ps.setInt(idx++, req.getItemId());
        }
        if (req.getCompanyId() != null) {
            ps.setInt(idx++, req.getCompanyId());
        }
        if (req.getOfficeId() != null) {
            ps.setInt(idx++, req.getOfficeId());
        }
        if (req.getRecyclingFee() != null) {
            ps.setInt(idx++, req.getRecyclingFee());
        }
        if (req.getDisposalSiteId() != null) {
            ps.setInt(idx++, req.getDisposalSiteId());
        }
        if (req.getState() != null) {
            ps.setInt(idx++, req.getState());
        }

        for (String num : req.getRecycleNumbers()) {
            ps.setString(idx++, num);
        }

        return idx;
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

    // public static int bindFindGroupCombo(PreparedStatement ps, String number) throws SQLException {
    //     int index = 1;
    //     ps.setInt(index++, Enums.state.DELETE.getCode());
    //     return index;
    // }

    public static int bindUpdateForDate(PreparedStatement ps, int id, LocalDate date, String editor, int index) throws SQLException {
        ps.setDate(index++, java.sql.Date.valueOf(date));
        ps.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));
        ps.setInt(index++, id);
        ps.setString(index++, editor);
        return index;
    }

    public static int bindInsertForDate(PreparedStatement ps, RecycleDateEntity r, String editor, int index) throws SQLException {
        ps.setString(index++, r.getNumber());
        ps.setString(index++, r.getMolding());
        ps.setDate(index++, java.sql.Date.valueOf(r.getDate()));
        ps.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(index++, editor);
        return index;
    }

    public static int bindDelete(PreparedStatement ps, int id, String editor, int index) throws SQLException {
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setString(index++, editor);
        return index;
    }

    public static int bindFindByBetween(PreparedStatement ps, LocalDate start, LocalDate end) throws SQLException {
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

    public static int bindSave(PreparedStatement pstmt, RecycleEntity entity, String editor) throws SQLException {
        int index = 1;
        // for (RecycleEntity entity : list) {
        //     if (entity.getRecycleId() > 0){
        //         index = RecycleParameterBinder.bindUpdate(pstmt, entity, editor, index);
        //     } else {
        //         index = RecycleParameterBinder.bindInsert(pstmt, entity, editor, index);
        //     }
        // }
        
        // return index;
        return RecycleParameterBinder.bindInsert(pstmt, entity, editor, index);
    }
    
    public static int bindUpdateForDate(PreparedStatement ps, RecycleDateEntity entity, String editor) throws SQLException {
        int index = 1;
        ps.setDate(index++, java.sql.Date.valueOf(entity.getDate()));
        ps.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(index++, entity.getNumber());
        ps.setString(index++, editor);
        // return RecycleParameterBinder.bindUpdateForDate(pstmt, entity.getId(), entity.getDate(), editor, index);
        // for (RecycleDateEntity entity : list) {
        //     if (entity.getRecycleId() > 0){
        //         index = RecycleParameterBinder.bindUpdateForDate(pstmt, entity.getRecycleId(), entity.getDate(), editor, index);
        //     } else {
        //         index = RecycleParameterBinder.bindInsertForDate(pstmt, entity, editor, index);
        //     }
        // }
        return index;
    }

    public static int bindDeleteByIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index++, editor); // 4. log
        return index;
    }

    public static int bindDownloadCsvByIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
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
