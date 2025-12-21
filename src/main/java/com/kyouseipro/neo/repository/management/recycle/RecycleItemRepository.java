package com.kyouseipro.neo.repository.management.recycle;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.management.recycle.RecycleItemEntity;
import com.kyouseipro.neo.mapper.management.recycle.RecycleItemEntityMapper;
import com.kyouseipro.neo.query.parameter.management.recycle.RecycleItemParameterBinder;
import com.kyouseipro.neo.query.sql.management.recycle.RecycleItemSqlBuilder;
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
        String sql = RecycleItemSqlBuilder.buildFindByIdSql();

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
        String sql = RecycleItemSqlBuilder.buildFindByCodeSql();
        
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
    public Integer deleteRecycleItemByIds(List<SimpleData> ids) {
        List<Integer> recycleItemIds = Utilities.createSequenceByIds(ids);
        String sql = RecycleItemSqlBuilder.buildDeleteRecycleItemForIdsSql(recycleItemIds.size());

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
    public List<RecycleItemEntity> downloadCsvRecycleItemByIds(List<SimpleData> ids) {
        List<Integer> recycleItemIds = Utilities.createSequenceByIds(ids);
        String sql = RecycleItemSqlBuilder.buildDownloadCsvRecycleItemForIdsSql(recycleItemIds.size());

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
    public Integer insertRecycleItem(RecycleItemEntity entity) {
        String sql = RecycleItemSqlBuilder.buildInsertRecycleItemSql();
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
    public Integer updateRecycleItem(RecycleItemEntity entity) {
        String sql = RecycleItemSqlBuilder.buildUpdateRecycleItemSql();

        Integer result = sqlRepository.executeUpdate(
            sql,
            pstmt -> RecycleItemParameterBinder.bindUpdateRecycleItemParameters(pstmt, entity)
        );

        return result; // 成功件数。0なら削除なし
    }
}