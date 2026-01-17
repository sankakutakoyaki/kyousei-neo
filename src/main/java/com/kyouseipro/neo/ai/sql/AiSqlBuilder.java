package com.kyouseipro.neo.ai.sql;

public class AiSqlBuilder {
    
    public static String findForQuestion() {
        return
            "SELECT TOP 5 " +
            "  c.ai_knowledge_chunk_id, " +
            "  c.ai_knowledge_id, " +
            "  c.content, " +
            "  c.priority " +
            "FROM ai_knowledge_chunk c " +
            "LEFT JOIN ai_knowledge_tag t " +
            "  ON t.ai_knowledge_chunk_id = c.ai_knowledge_chunk_id " +
            "  AND t.state = 0 " +
            "WHERE c.state = 0 " +
            "AND ( " +
            "  c.content LIKE ? " +
            "  OR t.tag LIKE ? " +
            ") " +
            "ORDER BY c.priority DESC, c.ai_knowledge_chunk_id DESC";
    }

    public static String findActiveByName() {
        return
            "SELECT system_prompt, user_prompt_format " +
            "FROM ai_prompt_template " +
            "WHERE state = 0 AND name = ?";
    }

    public static String insert() {
        return
            "INSERT INTO ai_ask_log " +
            "(question, used_chunks, answer) " +
            "VALUES (?, ?, ?)";
    }

    public static String findAllActive() {
        return
            "SELECT ai_knowledge_id, category, title " +
            "FROM ai_knowledge " +
            "WHERE state = 0 " +
            "ORDER BY ai_knowledge_id DESC";
    }
}
