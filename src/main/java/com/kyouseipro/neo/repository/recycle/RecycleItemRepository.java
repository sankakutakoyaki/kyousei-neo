package com.kyouseipro.neo.repository.recycle;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleItemEntity;
import com.kyouseipro.neo.mapper.recycle.RecycleItemEntityMapper;
import com.kyouseipro.neo.query.parameter.recycle.RecycleItemParameterBinder;
import com.kyouseipro.neo.query.sql.recycle.RecycleItemSqlBuilder;
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
    public RecycleItemEntity findById(int recycleItemId) {
        String sql = RecycleItemSqlBuilder.buildFindById();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> RecycleItemParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? RecycleItemEntityMapper.map(rs) : null,
            recycleItemId
        );
    } 

    /**
     * Codeによる取得。
     * @param code
     * @return IDから取得したEntityをかえす。
     */
    public RecycleItemEntity findByCode(int code) {
        String sql = RecycleItemSqlBuilder.buildFindByCode();
        
        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> RecycleItemParameterBinder.bindFindByCode(pstmt, comp),
            rs -> rs.next() ? RecycleItemEntityMapper.map(rs) : null,
            code
        );
    }

    /**
     * 削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public Integer deleteByIds(List<SimpleData> ids) {
        List<Integer> recycleItemIds = Utilities.createSequenceByIds(ids);
        String sql = RecycleItemSqlBuilder.buildDeleteByIds(recycleItemIds.size());

        return sqlRepository.executeUpdate(
            sql,
            ps -> RecycleItemParameterBinder.bindDeleteForIds(ps, recycleItemIds)
        );
        // return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<RecycleItemEntity> downloadCsvByIds(List<SimpleData> ids) {
        List<Integer> recycleItemIds = Utilities.createSequenceByIds(ids);
        String sql = RecycleItemSqlBuilder.buildDownloadCsvByIds(recycleItemIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> RecycleItemParameterBinder.bindDownloadCsvForIds(ps, recycleItemIds),
            RecycleItemEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public Integer insert(RecycleItemEntity entity) {
        String sql = RecycleItemSqlBuilder.buildInsert();
        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> RecycleItemParameterBinder.bindInsert(pstmt, emp),
            rs -> rs.next() ? rs.getInt("recycle_maker_id") : null,
            entity
        );
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public Integer update(RecycleItemEntity entity) {
        String sql = RecycleItemSqlBuilder.buildUpdate();

        Integer result = sqlRepository.executeUpdate(
            sql,
            pstmt -> RecycleItemParameterBinder.bindUpdate(pstmt, entity)
        );

        return result; // 成功件数。0なら削除なし
    }
}