package com.kyouseipro.neo.query.parameter.document;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.document.HistoryEntity;

public class HistoryParameterBinder {
    public static void bindInsertHistoryParameters(PreparedStatement pstmt, HistoryEntity history, String editor) throws SQLException {
        pstmt.setString(1, history.getUser_name());
        pstmt.setString(2, history.getTable_name());
        pstmt.setString(3, history.getAction());
        pstmt.setInt(4, history.getResult_code());
        pstmt.setString(5, history.getResult_message());
    }
}
