package com.kyouseipro.neo.ai.reepository;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.ai.entity.AiPromptTemplate;
import com.kyouseipro.neo.ai.mapper.AiPromptTemplateMapper;
import com.kyouseipro.neo.ai.parameter.AiParameterBinder;
import com.kyouseipro.neo.ai.sql.AiSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

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