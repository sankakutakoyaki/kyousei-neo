package com.kyouseipro.neo.common.history.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.common.history.entity.HistoryEntity;


public class HistoryEntityMapper {
    public static HistoryEntity map(ResultSet rs) throws SQLException {
        HistoryEntity entity = new HistoryEntity();
        entity.setHistory_id(rs.getInt("history_id"));
        entity.setExecutionDate(rs.getTimestamp("execution_date").toLocalDateTime());
        entity.setUser_name(rs.getString("user_name"));
        entity.setTable_name(rs.getString("table_name"));
        entity.setAction(rs.getString("action"));
        entity.setResultCode(rs.getInt("result_code"));
        entity.setResultMessage(rs.getString("result_messqage"));
        return entity;
    }

    public static HistoryEntity set(String userName, String tableName, String action, int resultCode, String resultMessage) {
        HistoryEntity entity = new HistoryEntity();
        entity.setUser_name(userName);
        entity.setTable_name(tableName);
        entity.setAction(action);
        entity.setResultCode(resultCode);
        entity.setResultMessage(resultMessage);
        return entity;
    }
}
