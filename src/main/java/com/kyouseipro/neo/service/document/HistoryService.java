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
    public Integer save(String userName, String tableName, String action, int resultCode, String resultMessage) {
        HistoryEntity entity = HistoryEntityMapper.set(userName, tableName, action, resultCode, resultMessage);
        return historyRepository.insertHistory(entity, userName);
    }
}
