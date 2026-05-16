package com.kyouseipro.neo.history.service;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.history.entity.HistoryEntity;
import com.kyouseipro.neo.history.mapper.HistoryEntityMapper;
import com.kyouseipro.neo.history.repository.HistoryRepository;

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
        return historyRepository.insert(entity, userName);
    }
}
