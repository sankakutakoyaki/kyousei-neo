package com.kyouseipro.neo.ai.repository;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.ai.entity.AiInsertParam;
import com.kyouseipro.neo.ai.parameter.AiParameterBinder;
import com.kyouseipro.neo.ai.sql.AiSqlBuilder;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AiAskLogRepository {

    private final SqlRepository sqlRepository;

    public void insert(String question, int usedChunks, String answer) {

        String sql = AiSqlBuilder.insert();
        AiInsertParam e = new AiInsertParam(question, usedChunks, answer);

        sqlRepository.executeUpdate(
            sql,
            ps -> AiParameterBinder.insert(ps, e)
        );
    }
}
