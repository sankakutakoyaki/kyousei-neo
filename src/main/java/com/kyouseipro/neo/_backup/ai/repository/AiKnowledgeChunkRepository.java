package com.kyouseipro.neo._backup.ai.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo._backup.ai.entity.AiKnowledgeChunk;
import com.kyouseipro.neo._backup.ai.mapper.AiKnowledgeChunkMapper;
import com.kyouseipro.neo._backup.ai.parameter.AiParameterBinder;
import com.kyouseipro.neo._backup.ai.sql.AiSqlBuilder;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AiKnowledgeChunkRepository {

    private final SqlRepository sqlRepository;
    /**
     * AI に渡す chunk 抽出
     */
    public List<AiKnowledgeChunk> findForQuestion(String question) {

        String sql = AiSqlBuilder.findForQuestion();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> AiParameterBinder.findForQuestion(ps, question),
            AiKnowledgeChunkMapper::map // ← ここで ResultSet を map
        );
    }
}