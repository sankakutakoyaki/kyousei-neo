package com.kyouseipro.neo.service;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.record.HistoryEntity;
import com.kyouseipro.neo.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DatabaseService { 
    private final SqlRepository sqlRepository;
    /**
     * 作業履歴を保存する
     * @param user_name
     * @param table_name
     * @param action
     * @param result_code
     * @param result_message
     * @return
     */
    public SimpleData saveHistory(String user_name, String table_name, String action, int result_code, String result_message) {
        HistoryEntity historyEntity = new HistoryEntity();
        historyEntity.setUser_name(user_name);
        historyEntity.setTable_name(table_name);
        historyEntity.setAction(action);
        historyEntity.setResult_code(result_code);
        historyEntity.setResult_message(result_message);
        String sqlString = historyEntity.getInsertString();
        return sqlRepository.excuteSqlString(sqlString);
    }

    /**
     * 
     * @param user_name
     * @param tableName
     * @param actioString
     * @return
     */
    public static String getInsertLogTableString(String user_name, String tableName, String actioString) {
        StringBuilder sb = new StringBuilder();
        sb.append("IF @NEW_ID > 0 BEGIN ");
        sb.append(HistoryEntity.insertString(user_name, tableName, actioString + "成功", "@NEW_ID", ""));
        sb.append("SELECT @NEW_ID as number, '作成しました' as text; END");
        sb.append(" ELSE BEGIN ");
        sb.append(HistoryEntity.insertString(user_name, tableName, actioString + "失敗", "@NEW_ID", ""));
        sb.append("SELECT 0 as number, '作成できませんでした' as text; END;");
        return sb.toString();
    }

    /**
     * 
     * @param user_name
     * @param tableName
     * @param actioString
     * @return
     */
    public static String getUpdateLogTableString(String user_name, String tableName, String actioString) {
        StringBuilder sb = new StringBuilder();
        sb.append("IF @ROW_COUNT > 0 BEGIN ");
        sb.append(HistoryEntity.insertString(user_name, tableName, actioString + "成功", "@ROW_COUNT", ""));
        sb.append("SELECT 200 as number, '変更しました' as text; END");
        sb.append(" ELSE BEGIN ");
        sb.append(HistoryEntity.insertString(user_name, tableName, actioString + "失敗", "@ROW_COUNT", ""));
        sb.append("SELECT 0 as number, '変更できませんでした' as text; END;");
        return sb.toString();
    }
}
