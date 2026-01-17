package com.kyouseipro.neo.ai.reepository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.ai.entity.AiKnowledge;
import com.kyouseipro.neo.ai.mapper.AiKnowledgeMapper;
import com.kyouseipro.neo.ai.sql.AiSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AiKnowledgeRepository {
    private final SqlRepository sqlRepository;

    public List<AiKnowledge> findAllActive() {

        String sql = AiSqlBuilder.findAllActive();

        return sqlRepository.findAll(
            sql,
            (ps, e) -> {},
            AiKnowledgeMapper::map // ← ここで ResultSet を map
        );
    }
    //     return sqlRepository.execute(
    //         sql,
    //         (ps, e) -> {},
    //         rs -> {
    //             List<AiKnowledge> list = new ArrayList<>();
    //             while (rs.next()) {
    //                 AiKnowledge k = new AiKnowledge();
    //                 k.setId(rs.getLong("ai_knowledge_id"));
    //                 k.setCategory(rs.getString("category"));
    //                 k.setTitle(rs.getString("title"));
    //                 list.add(k);
    //             }
    //             return list;
    //         },
    //         null
    //     );
    // }
}