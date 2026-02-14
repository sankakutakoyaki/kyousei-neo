package com.kyouseipro.neo.personnel.workingconditions.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.personnel.workingconditions.entity.WorkingConditionsEntity;

public class WorkingConditionsParameterBinder {

    public static int bindInsert(PreparedStatement pstmt, WorkingConditionsEntity w, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, w.getEmployeeId());
        pstmt.setInt(index++, w.getPaymentMethod());
        pstmt.setInt(index++, w.getPayType());
        pstmt.setInt(index++, w.getBaseSalary());
        pstmt.setInt(index++, w.getTransCost());
        pstmt.setInt(index++, w.getVersion());
        pstmt.setInt(index++, w.getState());

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, WorkingConditionsEntity w, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, w.getEmployeeId());
        pstmt.setInt(index++, w.getPaymentMethod());
        pstmt.setInt(index++, w.getPayType());
        pstmt.setInt(index++, w.getBaseSalary());
        pstmt.setInt(index++, w.getTransCost());
        pstmt.setInt(index++, w.getVersion() +1);
        pstmt.setInt(index++, w.getState());

        pstmt.setInt(index++, w.getWorkingConditionsId());
        pstmt.setInt(index++, w.getVersion());
        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindDeleteByIds(PreparedStatement ps, List<Integer> ids, String editor) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index, editor);
        return index;
    }

    public static int bindDownloadCsvByIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        ps.setInt(index, Enums.state.DELETE.getCode());
        return index;
    }
}
