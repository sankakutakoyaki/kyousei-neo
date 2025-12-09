package com.kyouseipro.neo.repository.recycle;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.recycle.RecycleMakerEntity;
import com.kyouseipro.neo.mapper.recycle.RecycleMakerEntityMapper;
import com.kyouseipro.neo.query.parameter.recycle.RecycleMakerParameterBinder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecycleMakerRepository {
    private final SqlRepository sqlRepository;
    
    /**
     * IDによる取得。
     * @param orderId
     * @return IDから取得したEntityをかえす。
     */
    public RecycleMakerEntity findById(String sql, int recycleMakerId) {
        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> RecycleMakerParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? RecycleMakerEntityMapper.map(rs) : null,
            recycleMakerId
        );
    }

    /**
     * 削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public Integer deleteRecycleMakerByIds(String sql, List<Integer> ids) {

        return sqlRepository.executeUpdate(
            sql,
            ps -> RecycleMakerParameterBinder.bindDeleteForIds(ps, ids)
        );
        // return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<RecycleMakerEntity> downloadCsvRecycleMakerByIds(String sql, List<Integer> ids) {

        return sqlRepository.findAll(
            sql,
            ps -> RecycleMakerParameterBinder.bindDownloadCsvForIds(ps, ids),
            RecycleMakerEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public Integer insertRecycleMaker(String sql, RecycleMakerEntity entity) {
        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> RecycleMakerParameterBinder.bindInsertRecycleMakerParameters(pstmt, emp),
            rs -> rs.next() ? rs.getInt("recycle_maker_id") : null,
            entity
        );
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public Integer updateRecycleMaker(String sql, RecycleMakerEntity entity) {
        Integer result = sqlRepository.executeUpdate(
            sql,
            pstmt -> RecycleMakerParameterBinder.bindUpdateRecycleMakerParameters(pstmt, entity)
        );

        return result; // 成功件数。0なら削除なし
    }
}
