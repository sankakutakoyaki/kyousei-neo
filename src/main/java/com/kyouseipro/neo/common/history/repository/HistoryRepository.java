package com.kyouseipro.neo.common.history.repository;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.history.entity.HistoryEntity;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HistoryRepository {
    private final SqlRepository sqlRepository;

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(HistoryEntity entity, String editor) {
        String sql = HistorySqlBuilder.buildInsert();
        return sqlRepository.queryOne(
            sql,
            (ps, e) -> HistoryParameterBinder.bindInsert(ps, e, editor),
            rs -> rs.getInt("history_id"),
            entity
        );
    }
}
