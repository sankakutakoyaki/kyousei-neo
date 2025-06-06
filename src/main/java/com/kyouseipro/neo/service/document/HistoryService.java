package com.kyouseipro.neo.service.document;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.document.HistoryEntity;
import com.kyouseipro.neo.mapper.document.HistoryEntityMapper;
import com.kyouseipro.neo.repository.document.HistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService { 
    private final HistoryRepository historyRepository;
    
    /**
     * 作業履歴を保存する
     * @param user_name
     * @param table_name
     * @param action
     * @param result_code
     * @param result_message
     * @return
     */
    public Integer saveHistory(String userName, String tableName, String action, int resultCode, String resultMessage) {
        HistoryEntity entity = HistoryEntityMapper.set(userName, tableName, action, resultCode, resultMessage);
        return historyRepository.insertHistory(entity, userName);
    }

    // /**
    //  * 
    //  * @param user_name
    //  * @param tableName
    //  * @param actioString
    //  * @return
    //  */
    // public static String getInsertLogTableString(String user_name, String tableName, String actioString) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("IF @NEW_ID > 0 BEGIN ");
    //     sb.append(HistoryEntity.insertString(user_name, tableName, actioString + "成功", "@NEW_ID", ""));
    //     sb.append("SELECT @NEW_ID as number, '作成しました' as text; END");
    //     sb.append(" ELSE BEGIN ");
    //     sb.append(HistoryEntity.insertString(user_name, tableName, actioString + "失敗", "@NEW_ID", ""));
    //     sb.append("SELECT 0 as number, '作成できませんでした' as text; END;");
    //     return sb.toString();
    // }

    // /**
    //  * 
    //  * @param user_name
    //  * @param tableName
    //  * @param actioString
    //  * @return
    //  */
    // public static String getUpdateLogTableString(String user_name, String tableName, String actioString) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("IF @ROW_COUNT > 0 BEGIN ");
    //     sb.append(HistoryEntity.insertString(user_name, tableName, actioString + "成功", "@ROW_COUNT", ""));
    //     sb.append("SELECT 200 as number, '変更しました' as text; END");
    //     sb.append(" ELSE BEGIN ");
    //     sb.append(HistoryEntity.insertString(user_name, tableName, actioString + "失敗", "@ROW_COUNT", ""));
    //     sb.append("SELECT 0 as number, '変更できませんでした' as text; END;");
    //     return sb.toString();
    // }
}
