package com.kyouseipro.neo.query.parameter.personnel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;

public class WorkingConditionsParameterBinder {

    public static int bindInsert(PreparedStatement pstmt, WorkingConditionsEntity w, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, w.getEmployee_id());
        pstmt.setInt(index++, w.getPayment_method());
        pstmt.setInt(index++, w.getPay_type());
        pstmt.setInt(index++, w.getBase_salary());
        pstmt.setInt(index++, w.getTrans_cost());
        pstmt.setTime(index++, Time.valueOf(w.getBasic_start_time()));
        pstmt.setTime(index++, Time.valueOf(w.getBasic_end_time()));
        pstmt.setInt(index++, w.getVersion());
        pstmt.setInt(index++, w.getState());

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, WorkingConditionsEntity w, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, w.getEmployee_id());
        pstmt.setInt(index++, w.getPayment_method());
        pstmt.setInt(index++, w.getPay_type());
        pstmt.setInt(index++, w.getBase_salary());
        pstmt.setInt(index++, w.getTrans_cost());
        pstmt.setTime(index++, Time.valueOf(w.getBasic_start_time()));
        pstmt.setTime(index++, Time.valueOf(w.getBasic_end_time()));
        pstmt.setInt(index++, w.getVersion());
        pstmt.setInt(index++, w.getState());

        pstmt.setInt(index++, w.getWorking_conditions_id());
        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindFindById(PreparedStatement ps, Integer workingConditionsId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, workingConditionsId);
        return index;
    }

    public static int bindFindByEmployeeId(PreparedStatement ps, Integer employeeId) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, employeeId);
        return index;
    }

    public static int bindFindAll(PreparedStatement ps, Void unused) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindDeleteByIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 1. SET state = ?
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setString(index, editor);
        return index;
    }

    public static int bindDownloadCsvByIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        ps.setInt(index++, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        for (Integer id : ids) {
            ps.setInt(index++, id); // 2. company_id IN (?, ?, ?)
        }
        ps.setInt(index, Enums.state.DELETE.getCode()); // 3. AND NOT (state = ?)
        return index;
    }
}
