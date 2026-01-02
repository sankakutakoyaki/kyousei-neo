package com.kyouseipro.neo.repository.document;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
import com.kyouseipro.neo.entity.document.HistoryEntity;
import com.kyouseipro.neo.query.parameter.document.HistoryParameterBinder;
import com.kyouseipro.neo.query.sql.document.HistorySqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

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

        // return sqlRepository.execute(
        //     sql,
        //     (pstmt, his) -> HistoryParameterBinder.bindInsert(pstmt, his, editor),
        //     rs -> rs.next() ? rs.getInt("history_id") : null,
        //     history
        // );
        try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> HistoryParameterBinder.bindInsert(ps, en, editor),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("history_id");

                    if (rs.next()) {
                        throw new IllegalStateException("ID取得結果が複数行です");
                    }
                    return id;
                },
                entity
            );
        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("このコードはすでに使用されています。");
            }
            throw e;
        }
    }
}
