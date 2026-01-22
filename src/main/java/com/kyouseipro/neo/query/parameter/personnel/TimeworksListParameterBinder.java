package com.kyouseipro.neo.query.parameter.personnel;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;

public class TimeworksListParameterBinder {

    public static int bindFindByTodaysByEmployeeId(PreparedStatement ps, Integer employeeId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, employeeId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, LocalDate.now().toString());
        return index;
    }

    public static int bindFindByBetweenByEmployeeId(PreparedStatement ps, Integer employeeId, LocalDate start, LocalDate end) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, employeeId);
        ps.setInt(index++, Enums.state.CREATE.getCode());
        ps.setString(index++, start.toString());
        ps.setString(index++, end.toString());
        return index;
    }

    public static int bindFindAllByBetweenByEmployeeId(PreparedStatement ps, Integer employeeId, LocalDate start, LocalDate end) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, employeeId);
        ps.setDate(index++, Date.valueOf(start));
        ps.setDate(index++, Date.valueOf(end));

        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, employeeId);
        ps.setDate(index++, Date.valueOf(start));
        ps.setDate(index++, Date.valueOf(end));
        return index;
    }

    public static int bindFindByBetweenSummary(PreparedStatement ps, LocalDate start, LocalDate end) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.COMPLETE.getCode());
        ps.setString(index++, start.toString());
        ps.setString(index++, end.toString());
        return index;
    }

    public static int bindFindByBetweenSummaryByOfficeId(PreparedStatement ps, Integer officeId, LocalDate start, LocalDate end) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.COMPLETE.getCode());
        ps.setInt(index++, officeId);
        ps.setString(index++, start.toString());
        ps.setString(index++, end.toString());
        return index;
    }

    public static int bindFindByDate(PreparedStatement ps, LocalDate date) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, date.toString());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindInsert(PreparedStatement pstmt, TimeworksListEntity t, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, t.getEmployee_id());
        pstmt.setString(index++, t.getWork_date().toString());
        pstmt.setInt(index++, Enums.state.DELETE.getCode());
        pstmt.setInt(index++, t.getEmployee_id());
        pstmt.setInt(index++, t.getCategory());
        pstmt.setString(index++, LocalDate.now().toString());
        if (t.getStart_date_time() != null) {
            pstmt.setTimestamp(
                index++,
                Timestamp.valueOf(t.getStart_date_time())
            );
        } else {
            pstmt.setNull(index++, Types.TIMESTAMP);
        }
        // pstmt.setTimestamp(index++, t.getStart_date_time() != null ? Timestamp.valueOf(t.getStart_date_time()) : null);
        pstmt.setTime(index++, t.getStart_time() != null ? Time.valueOf(t.getStart_time()) : Time.valueOf("00:00:00"));
        if (t.getEnd_date_time() != null) {
            pstmt.setTimestamp(
                index++,
                Timestamp.valueOf(t.getEnd_date_time())
            );
        } else {
            pstmt.setNull(index++, Types.TIMESTAMP);
        }
        // pstmt.setTimestamp(index++, t.getEnd_date_time() != null ? Timestamp.valueOf(t.getEnd_date_time()) : null);
        pstmt.setTime(index++, t.getEnd_time() != null ? Time.valueOf(t.getEnd_time()) : Time.valueOf("00:00:00"));
        pstmt.setTime(index++, t.getComp_start_time() != null ? Time.valueOf(t.getComp_start_time()) : Time.valueOf("00:00:00"));
        pstmt.setTime(index++, t.getComp_end_time() != null ? Time.valueOf(t.getComp_end_time()) : Time.valueOf("00:00:00"));
        String rest = t.getRest_time();
        if (rest == null || rest.isBlank()) {
            pstmt.setTime(index++, Time.valueOf("00:00:00"));
        } else {
            LocalTime lt = LocalTime.parse(rest); // "00:00" もOK
            pstmt.setTime(index++, Time.valueOf(lt));
        }
        pstmt.setInt(index++, t.getVersion());
        pstmt.setInt(index++, t.getState());

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, TimeworksListEntity t, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, t.getEmployee_id());
        pstmt.setInt(index++, t.getCategory());
        pstmt.setString(index++, t.getWork_date().toString());
        if (t.getStart_date_time() != null) {
            pstmt.setTimestamp(
                index++,
                Timestamp.valueOf(t.getStart_date_time())
            );
        } else {
            pstmt.setNull(index++, Types.TIMESTAMP);
        }
        // pstmt.setTimestamp(index++, t.getStart_date_time() != null ? Timestamp.valueOf(t.getStart_date_time()) : null);
        pstmt.setTime(index++, t.getStart_time() != null ? Time.valueOf(t.getStart_time()) : Time.valueOf("00:00:00"));
        if (t.getEnd_date_time() != null) {
            pstmt.setTimestamp(
                index++,
                Timestamp.valueOf(t.getEnd_date_time())
            );
        } else {
            pstmt.setNull(index++, Types.TIMESTAMP);
        }
        // pstmt.setTimestamp(index++, t.getEnd_date_time() != null ? Timestamp.valueOf(t.getEnd_date_time()) : null);
        pstmt.setTime(index++, t.getEnd_time() != null ? Time.valueOf(t.getEnd_time()) : Time.valueOf("00:00:00"));
        pstmt.setTime(index++, t.getComp_start_time() != null ? Time.valueOf(t.getComp_start_time()) : Time.valueOf("00:00:00"));
        pstmt.setTime(index++, t.getComp_end_time() != null ? Time.valueOf(t.getComp_end_time()) : Time.valueOf("00:00:00"));
        
        String rest = t.getRest_time();
        if (rest == null || rest.isBlank()) {
            pstmt.setTime(index++, Time.valueOf("00:00:00"));
        } else {
            LocalTime lt = LocalTime.parse(rest); // "00:00" もOK
            pstmt.setTime(index++, Time.valueOf(lt));
        }

        pstmt.setInt(index++, t.getVersion());
        pstmt.setInt(index++, t.getState());

        pstmt.setInt(index++, t.getTimeworks_id());
        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindReverseConfirm(PreparedStatement pstmt, int id, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, Enums.state.CREATE.getCode());
        pstmt.setInt(index++, id);
        pstmt.setString(index++, editor);
        return index;
    }

    // CSVダウンロード
    // public static int bindDownloadCsvByIdsFromBetween(PreparedStatement ps, List<Integer> ids, String start, String end) throws SQLException {
    public static int bindDownloadCsvByIdsFromBetween(PreparedStatement ps, List<Integer> ids, LocalDate start, LocalDate end) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.COMPLETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id); // IN (?, ?, ?)
        }
        ps.setDate(index++, Date.valueOf(start.toString()));
        ps.setDate(index++, Date.valueOf(end.toString()));

        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id); // IN (?, ?, ?)
        }
        ps.setDate(index++, Date.valueOf(start.toString()));
        ps.setDate(index++, Date.valueOf(end.toString()));
        return index;
    }
}
