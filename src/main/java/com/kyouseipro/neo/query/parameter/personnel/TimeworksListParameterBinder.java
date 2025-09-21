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

    public static void bindFindByTodaysEntityByEmployeeId(PreparedStatement ps, Integer employeeId) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
        // ps.setInt(4, Enums.state.DELETE.getCode());
        ps.setInt(4, employeeId);
        ps.setInt(5, Enums.state.DELETE.getCode());
        ps.setString(6, LocalDate.now().toString());
    }

    public static void bindFindByBetweenEntityByEmployeeId(PreparedStatement ps, Integer employeeId, LocalDate start, LocalDate end) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
        // ps.setInt(4, Enums.state.DELETE.getCode());
        ps.setInt(4, employeeId);
        ps.setInt(5, Enums.state.CREATE.getCode());
        ps.setString(6, start.toString());
        ps.setString(7, end.toString());
    }

    public static void bindFindAllByBetweenEntityByEmployeeId(PreparedStatement ps, Integer employeeId, LocalDate start, LocalDate end) throws SQLException {
        // ps.setInt(1, Enums.state.DELETE.getCode());
        // ps.setInt(2, Enums.state.DELETE.getCode());
        // ps.setInt(3, Enums.state.DELETE.getCode());
        // // ps.setInt(4, Enums.state.DELETE.getCode());
        // ps.setInt(4, employeeId);
        // ps.setInt(5, Enums.state.DELETE.getCode());
        // ps.setString(6, start.toString());
        // ps.setString(7, end.toString());

        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());//ps.setInt(index++, Enums.state.COMPLETE.getCode());
        ps.setInt(index++, employeeId);
        ps.setDate(index++, Date.valueOf(start));
        ps.setDate(index++, Date.valueOf(end));

        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, employeeId);
        ps.setDate(index++, Date.valueOf(start));
        ps.setDate(index++, Date.valueOf(end));
    }

    public static void bindFindByBetweenSummaryEntity(PreparedStatement ps, LocalDate start, LocalDate end) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.COMPLETE.getCode());
        ps.setString(3, start.toString());
        ps.setString(4, end.toString());
    }

    public static void bindFindByBetweenSummaryEntityByOfficeId(PreparedStatement ps, Integer officeId, LocalDate start, LocalDate end) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.COMPLETE.getCode());
        ps.setInt(3, officeId);
        ps.setString(4, start.toString());
        ps.setString(5, end.toString());
    }

    // 有給休暇取得リスト
    public static void bindFindPaidHolidayEntityByOfficeIdFromYear(PreparedStatement ps, Integer officeId, String year) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setString(2, year);
        ps.setString(3, year);        
        ps.setInt(4, Enums.state.DELETE.getCode());
        ps.setInt(5, officeId);
    }

    public static void bindFindPaidHolidayEntityByEmployeeIdFromYear(PreparedStatement ps, Integer employeeId, String year) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, employeeId);
        ps.setInt(3, Enums.state.DELETE.getCode());
        ps.setString(4, year);
        ps.setString(5, year);
    }

    public static void bindFindByDate(PreparedStatement ps, LocalDate date) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
        // ps.setInt(4, Enums.state.DELETE.getCode());
        ps.setString(4, date.toString());
        ps.setInt(5, Enums.state.DELETE.getCode());
    }

    public static void bindInsertParameters(PreparedStatement pstmt, TimeworksListEntity t, String editor) throws SQLException {
        pstmt.setInt(1, t.getEmployee_id());
        pstmt.setInt(2, t.getCategory());
        pstmt.setString(3, LocalDate.now().toString());
        pstmt.setTime(4, t.getStart_time() != null ? Time.valueOf(t.getStart_time()) : Time.valueOf("00:00:00"));
        pstmt.setTime(5, t.getEnd_time() != null ? Time.valueOf(t.getEnd_time()) : Time.valueOf("00:00:00"));
        pstmt.setTime(6, t.getComp_start_time() != null ? Time.valueOf(t.getComp_start_time()) : Time.valueOf("00:00:00"));
        pstmt.setTime(7, t.getComp_end_time() != null ? Time.valueOf(t.getComp_end_time()) : Time.valueOf("00:00:00"));
        String rest = t.getRest_time();
        if (rest == null || rest.isBlank()) {
            pstmt.setTime(8, Time.valueOf("00:00:00"));
        } else {
            LocalTime lt = LocalTime.parse(rest); // "00:00" もOK
            pstmt.setTime(8, Time.valueOf(lt));
        }
        pstmt.setInt(9, t.getVersion());
        pstmt.setInt(10, t.getState());

        pstmt.setString(11, editor);
    }

    public static void bindUpdateParameters(PreparedStatement pstmt, TimeworksListEntity t, String editor) throws SQLException {
        pstmt.setInt(1, t.getEmployee_id());
        pstmt.setInt(2, t.getCategory());
        pstmt.setString(3, t.getWork_date().toString());
        pstmt.setTime(4, t.getStart_time() != null ? Time.valueOf(t.getStart_time()) : Time.valueOf("00:00:00"));
        pstmt.setTime(5, t.getEnd_time() != null ? Time.valueOf(t.getEnd_time()) : Time.valueOf("00:00:00"));
        pstmt.setTime(6, t.getComp_start_time() != null ? Time.valueOf(t.getComp_start_time()) : Time.valueOf("00:00:00"));
        pstmt.setTime(7, t.getComp_end_time() != null ? Time.valueOf(t.getComp_end_time()) : Time.valueOf("00:00:00"));
        
        String rest = t.getRest_time();
        if (rest == null || rest.isBlank()) {
            pstmt.setTime(8, Time.valueOf("00:00:00"));
        } else {
            LocalTime lt = LocalTime.parse(rest); // "00:00" もOK
            pstmt.setTime(8, Time.valueOf(lt));
        }

        pstmt.setInt(9, t.getVersion());
        pstmt.setInt(10, t.getState());

        pstmt.setInt(11, t.getTimeworks_id());

        pstmt.setString(12, editor);
    }

    public static void bindReverseConfirmParameters(PreparedStatement pstmt, int id, String editor) throws SQLException {
        pstmt.setInt(1, Enums.state.CREATE.getCode());
        pstmt.setInt(2, id);
        pstmt.setString(3, editor);
    }

    // CSVダウンロード
    public static void bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids, String start, String end) throws SQLException {
        int index = 1;

        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.COMPLETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id); // IN (?, ?, ?)
        }
        // ps.setString(index++, start);
        // ps.setString(index++, end);
        ps.setDate(index++, Date.valueOf(start));
        ps.setDate(index++, Date.valueOf(end));

        // ps.setInt(index++, Enums.state.DELETE.getCode());
        // ps.setInt(index++, Enums.state.DELETE.getCode());

        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id); // IN (?, ?, ?)
        }
        ps.setDate(index++, Date.valueOf(start));
        ps.setDate(index++, Date.valueOf(end));
    }

    // 有給
    public static void bindInsertPaidHolidayParameters(PreparedStatement pstmt, PaidHolidayEntity p, String editor) throws SQLException {
        pstmt.setInt(1, p.getEmployee_id());
        if (p.getStart_date() != null) {
            pstmt.setDate(2, java.sql.Date.valueOf(p.getStart_date()));
        } else {
            pstmt.setNull(2, java.sql.Types.DATE);
        }
        if (p.getEnd_date() != null) {
            pstmt.setDate(3, java.sql.Date.valueOf(p.getEnd_date()));
        } else {
            pstmt.setNull(3, java.sql.Types.DATE);
        }
        pstmt.setInt(4, p.getPermit_employee_id());
        pstmt.setString(5, p.getReason());
        pstmt.setInt(6, p.getVersion());
        pstmt.setInt(7, p.getState());

        pstmt.setInt(8, p.getEmployee_id());
        pstmt.setInt(9, Enums.state.DELETE.getCode());
        pstmt.setDate(10, java.sql.Date.valueOf(p.getEnd_date()));
        pstmt.setDate(11, java.sql.Date.valueOf(p.getStart_date()));

        pstmt.setString(12, editor);
    }

    public static void bindDeletePaidHolidayParameters(PreparedStatement pstmt, int id, String editor) throws SQLException {
        pstmt.setInt(1, Enums.state.DELETE.getCode());
        pstmt.setInt(2, id);

        pstmt.setString(3, editor);
    }
}
