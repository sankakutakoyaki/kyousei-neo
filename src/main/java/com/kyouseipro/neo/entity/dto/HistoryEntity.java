package com.kyouseipro.neo.entity.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HistoryEntity {
    private int history_id;
    private LocalDateTime executionDate;
    private String user_name;
    private String table_name;
    private String action;
    private int resultCode; // 2000番台以外はエラー
    private String resultMessage;
}
