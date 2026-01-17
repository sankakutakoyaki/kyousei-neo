package com.kyouseipro.neo.ai.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.ai.entity.AiPromptTemplate;

public class AiPromptTemplateMapper {
    public static AiPromptTemplate map(ResultSet rs) throws SQLException {
        AiPromptTemplate t = new AiPromptTemplate();
        t.systemPrompt = rs.getString("system_prompt");
        t.userPromptFormat = rs.getString("user_prompt_format");
        return t;
    }
}
