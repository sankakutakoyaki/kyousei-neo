package com.kyouseipro.neo.service;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.record.HistoryEntity;
import com.kyouseipro.neo.repository.SqlRepositry;

@Service
public class DatabaseService { 
    /**
     * 作業履歴を保存する
     * @param user_name
     * @param table_name
     * @param action
     * @param result_code
     * @param result_message
     * @return
     */
    public static SimpleData saveHistory(String user_name, String table_name, String action, int result_code, String result_message) {
        HistoryEntity historyEntity = new HistoryEntity();
        historyEntity.setUser_name(user_name);
        historyEntity.setTable_name(table_name);
        historyEntity.setAction(action);
        historyEntity.setResult_code(result_code);
        historyEntity.setResult_message(result_message);
        String sqlString = historyEntity.getInsertString();
        return SqlRepositry.excuteSqlString(sqlString);
    }
}
