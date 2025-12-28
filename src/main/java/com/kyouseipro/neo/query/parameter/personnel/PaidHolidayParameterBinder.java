package com.kyouseipro.neo.query.parameter.personnel;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.PaidHolidayEntity;

public class PaidHolidayParameterBinder {
    
    // 有給
    public static int bindInsert(PreparedStatement pstmt, PaidHolidayEntity p, String editor) throws SQLException {
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

    public static int bindDelete(PreparedStatement pstmt, int id, String editor) throws SQLException {
        int index = 1;
        pstmt.setInt(index++, Enums.state.DELETE.getCode());
        pstmt.setInt(index++, id);

        pstmt.setString(index++, editor);
        return index;
    }

    // 有給休暇取得リスト
    public static int bindFindByOfficeIdFromYear(PreparedStatement ps, Integer officeId, String year) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, year);
        ps.setString(index++, year);        
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, officeId);
        return index;
    }

    public static int bindFindByEmployeeIdFromYear(PreparedStatement ps, Integer employeeId, String year) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, employeeId);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setString(index++, year);
        ps.setString(index++, year);
        return index;
    }
}
