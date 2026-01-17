package com.kyouseipro.neo.ai.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.ai.entity.AiKnowledge;

public class AiKnowledgeMapper {
    public static AiKnowledge map(ResultSet rs) throws SQLException {
        AiKnowledge k = new AiKnowledge();
        k.setId(rs.getLong("ai_knowledge_id"));
        k.setCategory(rs.getString("category"));
        k.setTitle(rs.getString("title"));
        return k;
    }
}
