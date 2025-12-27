package com.kyouseipro.neo.repository.sales;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.order.WorkContentEntity;
import com.kyouseipro.neo.mapper.order.WorkContentEntityMapper;
import com.kyouseipro.neo.query.parameter.sales.WorkContentParameterBinder;
import com.kyouseipro.neo.query.sql.sales.WorkContentSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkContentRepository {
    private final SqlRepository sqlRepository;
    
    public List<WorkContentEntity> findAllByOrderId(int id, String editor) {
        String sql = WorkContentSqlBuilder.buildFindAllByOrderIdSql();

        return sqlRepository.findAll(
            sql,
            ps -> WorkContentParameterBinder.bindFindAllByOrderId(ps, id),
            WorkContentEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
