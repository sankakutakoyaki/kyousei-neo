package com.kyouseipro.neo.ai.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.kyouseipro.neo.ai.entity.AiKnowledgeChunk;

public class AiKnowledgeChunkMapper {
    public static AiKnowledgeChunk map(ResultSet rs) throws SQLException {
        AiKnowledgeChunk c = new AiKnowledgeChunk();
        c.id = rs.getLong("ai_knowledge_chunk_id");
        c.knowledgeId = rs.getLong("ai_knowledge_id");
        c.content = rs.getString("content");
        c.priority = rs.getInt("priority");
        return c;
    }
}
