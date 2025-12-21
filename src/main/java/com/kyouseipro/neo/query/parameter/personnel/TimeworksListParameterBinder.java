package com.kyouseipro.neo.query.parameter.personnel;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.PaidHolidayEntity;
import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;

public class TimeworksListParameterBinder {

    public static int bindFindByTodaysEntityByEmployeeId(PreparedStatement ps, Integer employeeId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, employeeId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, LocalDate.now().toString());
        return index;
    }

    public static int bindFindByBetweenEntityByEmployeeId(PreparedStatement ps, Integer employeeId, LocalDate start, LocalDate end) throws SQLException {
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

    public static int bindFindAllByBetweenEntityByEmployeeId(PreparedStatement ps, Integer employeeId, LocalDate start, LocalDate end) throws SQLException {
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

    public static int bindFindByBetweenSummaryEntity(PreparedStatement ps, LocalDate start, LocalDate end) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.COMPLETE.getCode());
        ps.setString(index++, start.toString());
        ps.setString(index++, end.toString());
        return index;
    }

    public static int bindFindByBetweenSummaryEntityByOfficeId(PreparedStatement ps, Integer officeId, LocalDate start, LocalDate end) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.COMPLETE.getCode());
        ps.setInt(index++, officeId);
        ps.setString(index++, start.toString());
        ps.setString(index++, end.toString());
        return index;
    }

    // 有給休暇取得リスト
    public static int bindFindPaidHolidayEntityByOfficeIdFromYear(PreparedStatement ps, Integer officeId, String year) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, year);
        ps.setString(index++, year);        
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, officeId);
        return index;
    }

    public static int bindFindPaidHolidayEntityByEmployeeIdFromYear(PreparedStatement ps, Integer employeeId, String year) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, employeeId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, year);
        ps.setString(index++, year);
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

    public static int bindInsertParameters(PreparedStatement pstmt, TimeworksListEntity t, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, t.getEmployee_id());
        pstmt.setString(index++, t.getWork_date().toString());
        pstmt.setInt(index++, Enums.state.DELETE.getCode());
        pstmt.setInt(index++, t.getEmployee_id());
        pstmt.setInt(index++, t.getCategory());
        pstmt.setString(index++, LocalDate.now().toString());
        pstmt.setTime(index++, t.getStart_time() != null ? Time.valueOf(t.getStart_time()) : Time.valueOf("00:00:00"));
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

    public static int bindUpdateParameters(PreparedStatement pstmt, TimeworksListEntity t, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, t.getEmployee_id());
        pstmt.setInt(index++, t.getCategory());
        pstmt.setString(index++, t.getWork_date().toString());
        pstmt.setTime(index++, t.getStart_time() != null ? Time.valueOf(t.getStart_time()) : Time.valueOf("00:00:00"));
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

    public static int bindReverseConfirmParameters(PreparedStatement pstmt, int id, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, Enums.state.CREATE.getCode());
        pstmt.setInt(index++, id);
        pstmt.setString(index++, editor);
        return index;
    }

    // CSVダウンロード
    public static int bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids, String start, String end) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.COMPLETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id); // IN (?, ?, ?)
        }
        ps.setDate(index++, Date.valueOf(start));
        ps.setDate(index++, Date.valueOf(end));

        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id); // IN (?, ?, ?)
        }
        ps.setDate(index++, Date.valueOf(start));
        ps.setDate(index++, Date.valueOf(end));
        return index;
    }

    // 有給
    public static int bindInsertPaidHolidayParameters(PreparedStatement pstmt, PaidHolidayEntity p, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, p.getEmployee_id());
        if (p.getStart_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(p.getStart_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        if (p.getEnd_date() != null) {
            pstmt.setDate(index++, java.sql.Date.valueOf(p.getEnd_date()));
        } else {
            pstmt.setNull(index++, java.sql.Types.DATE);
        }
        pstmt.setInt(index++, p.getPermit_employee_id());
        pstmt.setString(index++, p.getReason());
        pstmt.setInt(index++, p.getVersion());
        pstmt.setInt(index++, p.getState());

        pstmt.setInt(index++, p.getEmployee_id());
        pstmt.setInt(index++, Enums.state.DELETE.getCode());
        pstmt.setDate(index++, java.sql.Date.valueOf(p.getEnd_date()));
        pstmt.setDate(index++, java.sql.Date.valueOf(p.getStart_date()));

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindDeletePaidHolidayParameters(PreparedStatement pstmt, int id, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, Enums.state.DELETE.getCode());
        pstmt.setInt(index++, id);

        pstmt.setString(index++, editor);
        return index;
    }
}
