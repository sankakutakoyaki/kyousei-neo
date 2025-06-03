package com.kyouseipro.neo.query.parameter.personnel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;

public class WorkingConditionsParameterBinder {

    public static void bindInsertParameters(PreparedStatement pstmt, WorkingConditionsEntity w, String editor) throws SQLException {
        pstmt.setInt(1, w.getEmployee_id());
        pstmt.setInt(2, w.getCode());
        pstmt.setInt(3, w.getCategory());
        pstmt.setInt(4, w.getPayment_method());
        pstmt.setInt(5, w.getPay_type());
        pstmt.setInt(6, w.getBase_salary());
        pstmt.setInt(7, w.getTrans_cost());
        pstmt.setTime(8, Time.valueOf(w.getBasic_start_time()));
        pstmt.setTime(9, Time.valueOf(w.getBasic_end_time()));
        pstmt.setInt(10, w.getVersion());
        pstmt.setInt(11, w.getState());

        pstmt.setString(12, editor);
    }

    public static void bindUpdateParameters(PreparedStatement pstmt, WorkingConditionsEntity w, String editor) throws SQLException {
        pstmt.setInt(1, w.getEmployee_id());
        pstmt.setInt(2, w.getCode());
        pstmt.setInt(3, w.getCategory());
        pstmt.setInt(4, w.getPayment_method());
        pstmt.setInt(5, w.getPay_type());
        pstmt.setInt(6, w.getBase_salary());
        pstmt.setInt(7, w.getTrans_cost());
        pstmt.setTime(8, Time.valueOf(w.getBasic_start_time()));
        pstmt.setTime(9, Time.valueOf(w.getBasic_end_time()));
        pstmt.setInt(10, w.getVersion());
        pstmt.setInt(11, w.getState());

        pstmt.setInt(12, w.getWorking_conditions_id());

        pstmt.setString(13, editor);
    }

    public static void bindFindById(PreparedStatement ps, Integer workingConditionsId) throws SQLException {
        ps.setInt(1, workingConditionsId);
        ps.setInt(2, Enums.state.DELETE.getCode());
    }

    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
    }
}

