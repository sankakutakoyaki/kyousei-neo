package com.kyouseipro.neo.query.parameter.work;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.work.WorkPriceEntity;

public class WorkPriceParameterBinder {
    public static int bindInsert(PreparedStatement pstmt, WorkPriceEntity w, String editor, int index) throws SQLException {
        pstmt.setInt(index++, w.getWork_item_id());
        pstmt.setInt(index++, w.getCompany_id());
        pstmt.setInt(index++, w.getPrice());
        pstmt.setInt(index++, w.getVersion());
        pstmt.setInt(index++, w.getState());

        pstmt.setString(index++, editor);
        return index;
    }

    public static int bindUpdate(PreparedStatement pstmt, WorkPriceEntity w, String editor, int index) throws SQLException {
        pstmt.setInt(index++, w.getWork_item_id());
        pstmt.setInt(index++, w.getCompany_id());
        pstmt.setInt(index++, w.getPrice());
        pstmt.setInt(index++, w.getVersion() +1);
        pstmt.setInt(index++, w.getState());

        pstmt.setInt(index++, w.getWork_price_id()); // WHERE句
        pstmt.setInt(index++, w.getVersion());
        pstmt.setString(index++, editor);          // ログ用
        return index;
    }

    public static int bindFindById(PreparedStatement ps, int id) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }


    public static int bindFindAllByCompanyId(PreparedStatement ps, int id) throws SQLException {
        int index = 1;
        ps.setInt(index++, id);
        ps.setInt(index++, id);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, id);
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        return index;
    }

    public static int bindDownloadCsvForIds(PreparedStatement ps, List<Integer> ids) throws SQLException {
        int index = 1;
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        ps.setInt(index++, Enums.state.DELETE.getCode());
        for (Integer id : ids) {
            ps.setInt(index++, id); // company_id IN (?, ?, ?)
        }
        ps.setInt(index++, Enums.state.DELETE.getCode()); // AND NOT (state = ?)
        return index;
    }
}
