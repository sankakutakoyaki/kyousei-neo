package com.kyouseipro.neo.repository.document;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.document.HistoryEntity;
import com.kyouseipro.neo.query.parameter.document.HistoryParameterBinder;
import com.kyouseipro.neo.query.sql.document.HistorySqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HistoryRepository {
    private final SqlRepository sqlRepository;

    public Integer insertHistory(HistoryEntity history, String editor) {
        String sql = HistorySqlBuilder.buildInsertHistorySql();

        return sqlRepository.execute(
            sql,
            (pstmt, his) -> HistoryParameterBinder.bindInsertHistoryParameters(pstmt, his, editor),
            rs -> rs.next() ? rs.getInt("history_id") : null,
            history
        );
    }
}
