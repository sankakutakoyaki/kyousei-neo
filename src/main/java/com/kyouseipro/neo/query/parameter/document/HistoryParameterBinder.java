package com.kyouseipro.neo.query.parameter.document;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.document.HistoryEntity;

public class HistoryParameterBinder {
    public static int bindInsert(PreparedStatement pstmt, HistoryEntity history, String editor) throws SQLException {
        int index = 1;
        pstmt.setString(index++, history.getUser_name());
        pstmt.setString(index++, history.getTable_name());
        pstmt.setString(index++, history.getAction());
        pstmt.setInt(index++, history.getResult_code());
        pstmt.setString(index++, history.getResult_message());
        return index;
    }
}
