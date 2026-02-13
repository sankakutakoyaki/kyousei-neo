package com.kyouseipro.neo.sales.order.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.sales.order.entity.WorkContentEntity;
import com.kyouseipro.neo.sales.order.mapper.WorkContentEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkContentRepository {
    private final SqlRepository sqlRepository;

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<WorkContentEntity> findAllByOrderId(int id, String editor) {
        String sql = WorkContentSqlBuilder.buildFindAllByOrderId();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> WorkContentParameterBinder.bindFindAllByOrderId(ps, id),
            WorkContentEntityMapper::map
        );
    }
}
