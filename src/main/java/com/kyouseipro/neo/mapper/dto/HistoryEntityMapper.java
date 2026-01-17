package com.kyouseipro.neo.mapper.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.entity.dto.HistoryEntity;

public class HistoryEntityMapper {
    public static HistoryEntity map(ResultSet rs) throws SQLException {
        HistoryEntity entity = new HistoryEntity();
        entity.setHistory_id(rs.getInt("history_id"));
        entity.setExecution_date(rs.getTimestamp("execution_date").toLocalDateTime());
        entity.setUser_name(rs.getString("user_name"));
        entity.setTable_name(rs.getString("table_name"));
        entity.setAction(rs.getString("action"));
        entity.setResult_code(rs.getInt("result_code"));
        entity.setResult_message(rs.getString("result_messqage"));
        return entity;
    }

    public static HistoryEntity set(String userName, String tableName, String action, int resultCode, String resultMessage) {
        HistoryEntity entity = new HistoryEntity();
        entity.setUser_name(userName);
        entity.setTable_name(tableName);
        entity.setAction(action);
        entity.setResult_code(resultCode);
        entity.setResult_message(resultMessage);
        return entity;
    }
}
