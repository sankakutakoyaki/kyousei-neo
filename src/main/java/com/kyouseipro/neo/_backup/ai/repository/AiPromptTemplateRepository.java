package com.kyouseipro.neo._backup.ai.repository;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo._backup.ai.entity.AiPromptTemplate;
import com.kyouseipro.neo._backup.ai.mapper.AiPromptTemplateMapper;
import com.kyouseipro.neo._backup.ai.parameter.AiParameterBinder;
import com.kyouseipro.neo._backup.ai.sql.AiSqlBuilder;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AiPromptTemplateRepository {

    private final SqlRepository sqlRepository;

    public AiPromptTemplate findActiveByName(String name) {

        String sql = AiSqlBuilder.findActiveByName();

        return sqlRepository.executeRequired(
            sql,
            (ps, n) -> AiParameterBinder.findActiveByName(ps, n),
            rs -> rs.next() ? AiPromptTemplateMapper.map(rs) : null,
            name
        );
    }
}