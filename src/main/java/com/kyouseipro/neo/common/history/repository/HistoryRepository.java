package com.kyouseipro.neo.common.history.repository;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.exception.BusinessException;
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

        // return sqlRepository.executeRequired(
        //     sql,
        //     (ps, en) -> HistoryParameterBinder.bindInsert(ps, en, editor),
        //     rs -> {
        //         if (!rs.next()) {
        //             throw new BusinessException("登録に失敗しました");
        //         }
        //         int id = rs.getInt("history_id");

        //         if (rs.next()) {
        //             throw new IllegalStateException("ID取得結果が複数行です");
        //         }
        //         return id;
        //     },
        //     entity
        // );
        return sqlRepository.query(
            sql,
            ps-> HistoryParameterBinder.bindInsert(ps, entity, editor),
            rs -> {
                if (!rs.next()) {
                    throw new BusinessException("登録に失敗しました");
                }
                int id = rs.getInt("history_id");

                if (rs.next()) {
                    throw new IllegalStateException("ID取得結果が複数行です");
                }
                return id;
            }
        );
    }
}
