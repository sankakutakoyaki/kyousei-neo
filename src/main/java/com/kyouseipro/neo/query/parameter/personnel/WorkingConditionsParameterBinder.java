package com.kyouseipro.neo.query.parameter.personnel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;

public class WorkingConditionsParameterBinder {

    public static void bindInsertParameters(PreparedStatement pstmt, WorkingConditionsEntity w, String editor) throws SQLException {
        pstmt.setInt(1, w.getEmployee_id());
        // pstmt.setInt(2, w.getCode());
        // pstmt.setInt(3, w.getCategory());
        pstmt.setInt(2, w.getPayment_method());
        pstmt.setInt(3, w.getPay_type());
        pstmt.setInt(4, w.getBase_salary());
        pstmt.setInt(5, w.getTrans_cost());
        pstmt.setTime(6, Time.valueOf(w.getBasic_start_time()));
        pstmt.setTime(7, Time.valueOf(w.getBasic_end_time()));
        pstmt.setInt(8, w.getVersion());
        pstmt.setInt(9, w.getState());

        pstmt.setString(10, editor);
    }

    public static void bindUpdateParameters(PreparedStatement pstmt, WorkingConditionsEntity w, String editor) throws SQLException {
        pstmt.setInt(1, w.getEmployee_id());
        // pstmt.setInt(2, w.getCode());
        // pstmt.setInt(3, w.getCategory());
        pstmt.setInt(2, w.getPayment_method());
        pstmt.setInt(3, w.getPay_type());
        pstmt.setInt(4, w.getBase_salary());
        pstmt.setInt(5, w.getTrans_cost());
        pstmt.setTime(6, Time.valueOf(w.getBasic_start_time()));
        pstmt.setTime(7, Time.valueOf(w.getBasic_end_time()));
        pstmt.setInt(8, w.getVersion());
        pstmt.setInt(9, w.getState());

        pstmt.setInt(10, w.getWorking_conditions_id());

        pstmt.setString(11, editor);
    }

    public static void bindFindById(PreparedStatement ps, Integer workingConditionsId) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
        ps.setInt(4, workingConditionsId);        
    }

    public static void bindFindByEmployeeId(PreparedStatement ps, Integer employeeId) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
        ps.setInt(2, Enums.state.DELETE.getCode());
        ps.setInt(3, Enums.state.DELETE.getCode());
        ps.setInt(4, employeeId);        
    }

    public static void bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        ps.setInt(1, Enums.state.DELETE.getCode());
    }

    public static void bindDeleteForIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index, editor);
    }

    public static void bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
    }
}
