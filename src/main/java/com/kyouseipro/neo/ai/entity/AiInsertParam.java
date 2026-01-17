package com.kyouseipro.neo.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiInsertParam {
    private String question;
    private int usedChunks;
    private String answer;
}