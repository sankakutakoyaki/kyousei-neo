package com.kyouseipro.neo.ai.reepository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.ai.entity.AiKnowledgeChunk;
import com.kyouseipro.neo.ai.mapper.AiKnowledgeChunkMapper;
import com.kyouseipro.neo.ai.parameter.AiParameterBinder;
import com.kyouseipro.neo.ai.sql.AiSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

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