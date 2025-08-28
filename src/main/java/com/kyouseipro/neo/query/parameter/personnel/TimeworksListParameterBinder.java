package com.kyouseipro.neo.query.parameter.personnel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;

public class TimeworksListParameterBinder {

    public static void bindFindByTodaysEntityByEmployeeId(PreparedStatement ps, Integer employeeId) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, employeeId);
        ps.setInt(4, Enums.state.DELETE.getCode());
        ps.setString(5, LocalDate.now().toString());
    }

    public static void bindFindByDate(PreparedStatement ps, LocalDate date) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setString(3, date.toString());
        ps.setInt(4, Enums.state.DELETE.getCode());
    }

    public static void bindInsertParameters(PreparedStatement pstmt, TimeworksListEntity t, String editor) throws SQLException {
        pstmt.setInt(1, t.getEmployee_id());
        pstmt.setInt(2, t.getCategory());
        pstmt.setString(3, LocalDate.now().toString());
        pstmt.setTime(4, t.getStart_time() != null ? Time.valueOf(t.getStart_time()) : Time.valueOf("00:00:00"));
        pstmt.setTime(5, t.getEnd_time() != null ? Time.valueOf(t.getEnd_time()) : Time.valueOf("00:00:00"));
        pstmt.setInt(6, t.getVersion());
        pstmt.setInt(7, t.getState());

        pstmt.setString(8, editor);
    }

    public static void bindUpdateParameters(PreparedStatement pstmt, TimeworksListEntity t, String editor) throws SQLException {
        pstmt.setInt(1, t.getEmployee_id());
        pstmt.setInt(2, t.getCategory());
        pstmt.setString(3, LocalDate.now().toString());
        pstmt.setTime(4, t.getStart_time() != null ? Time.valueOf(t.getStart_time()) : Time.valueOf("00:00:00"));
        pstmt.setTime(5, t.getEnd_time() != null ? Time.valueOf(t.getEnd_time()) : Time.valueOf("00:00:00"));
        pstmt.setInt(6, t.getVersion());
        pstmt.setInt(7, t.getState());

        pstmt.setInt(8, t.getTimeworks_id());

        pstmt.setString(9, editor);
    }
}
