package com.kyouseipro.neo.query.parameter.personnel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;

public class TimeworksListParameterBinder {

    public static void bindFindByTodaysEntityByEmployeeId(PreparedStatement ps, Integer employeeId) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
        ps.setInt(4, employeeId);
        ps.setInt(5, Enums.state.DELETE.getCode());
        ps.setString(6, LocalDate.now().toString());
    }

    public static void bindFindByBetweenEntityByEmployeeId(PreparedStatement ps, Integer employeeId, LocalDate start, LocalDate end) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
        ps.setInt(4, employeeId);
        ps.setInt(5, Enums.state.DELETE.getCode());
        ps.setString(6, start.toString());
        ps.setString(7, end.toString());
    }

    public static void bindFindByDate(PreparedStatement ps, LocalDate date) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
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
        // pstmt.setTime(8, t.getRest_time() != null ? Time.valueOf(t.getRest_time()) : Time.valueOf("00:00:00"));
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
        // System.out.println(t.getRest_time());
        // pstmt.setTime(8, t.getRest_time() != null ? Time.valueOf(t.getRest_time()) : Time.valueOf("00:00:00"));
        
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

    // public static void bindUpdateListParameters(PreparedStatement pstmt, List<TimeworksListEntity> list, String editor) throws SQLException {
    //     for (TimeworksListEntity t : list) {
    //         pstmt.setInt(1, t.getEmployee_id());
    //         pstmt.setInt(2, t.getCategory());
    //         pstmt.setString(3, LocalDate.now().toString());
    //         pstmt.setTime(4, t.getStart_time() != null ? Time.valueOf(t.getStart_time()) : Time.valueOf("00:00:00"));
    //         pstmt.setTime(5, t.getEnd_time() != null ? Time.valueOf(t.getEnd_time()) : Time.valueOf("00:00:00"));
    //         pstmt.setTime(6, t.getComp_start_time() != null ? Time.valueOf(t.getComp_start_time()) : Time.valueOf("00:00:00"));
    //         pstmt.setTime(7, t.getComp_end_time() != null ? Time.valueOf(t.getComp_end_time()) : Time.valueOf("00:00:00"));
    //         pstmt.setTime(8, t.getRest_time() != null ? Time.valueOf(t.getRest_time()) : Time.valueOf("00:00:00"));
    //         pstmt.setInt(9, t.getVersion());
    //         pstmt.setInt(10, t.getState());

    //         pstmt.setInt(11, t.getTimeworks_id());

    //         pstmt.setString(12, editor);
    //     };
    // }
}
