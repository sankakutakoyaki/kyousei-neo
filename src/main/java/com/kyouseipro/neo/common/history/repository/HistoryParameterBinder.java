package com.kyouseipro.neo.common.history.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.common.history.entity.HistoryEntity;


public class HistoryParameterBinder {
    public static int bindInsert(PreparedStatement pstmt, HistoryEntity history, String editor) throws SQLException {
        int index = 1;
        pstmt.setString(index++, history.getUser_name());
        pstmt.setString(index++, history.getTable_name());
        pstmt.setString(index++, history.getAction());
        pstmt.setInt(index++, history.getResultCode());
        pstmt.setString(index++, history.getResultMessage());
        return index;
    }
}
