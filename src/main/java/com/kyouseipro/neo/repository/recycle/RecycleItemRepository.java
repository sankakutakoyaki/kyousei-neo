package com.kyouseipro.neo.repository.recycle;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.recycle.RecycleItemEntity;
import com.kyouseipro.neo.mapper.recycle.RecycleItemEntityMapper;
import com.kyouseipro.neo.query.parameter.recycle.RecycleItemParameterBinder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecycleItemRepository {
    private final SqlRepository sqlRepository;
    
    /**
     * IDによる取得。
     * @param orderId
     * @return IDから取得したEntityをかえす。
     */
    public RecycleItemEntity findById(String sql, int recycleItemId) {
        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> RecycleItemParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? RecycleItemEntityMapper.map(rs) : null,
            recycleItemId
        );
    }

    /**
     * 削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public Integer deleteRecycleItemByIds(String sql, List<Integer> ids) {

        return sqlRepository.executeUpdate(
            sql,
            ps -> RecycleItemParameterBinder.bindDeleteForIds(ps, ids)
        );
        // return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<RecycleItemEntity> downloadCsvRecycleItemByIds(String sql, List<Integer> ids) {

        return sqlRepository.findAll(
            sql,
            ps -> RecycleItemParameterBinder.bindDownloadCsvForIds(ps, ids),
            RecycleItemEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public Integer insertRecycleItem(String sql, RecycleItemEntity entity) {
        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> RecycleItemParameterBinder.bindInsertRecycleItemParameters(pstmt, emp),
            rs -> rs.next() ? rs.getInt("recycle_maker_id") : null,
            entity
        );
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public Integer updateRecycleItem(String sql, RecycleItemEntity entity) {
        Integer result = sqlRepository.executeUpdate(
            sql,
            pstmt -> RecycleItemParameterBinder.bindUpdateRecycleItemParameters(pstmt, entity)
        );

        return result; // 成功件数。0なら削除なし
    }
}