package com.kyouseipro.neo.repository.work;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.work.WorkItemEntity;
import com.kyouseipro.neo.mapper.data.SimpleDataMapper;
import com.kyouseipro.neo.mapper.work.WorkItemEntityMapper;
import com.kyouseipro.neo.query.parameter.work.WorkItemParameterBinder;
import com.kyouseipro.neo.query.sql.work.WorkItemSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkItemRepository {
    private final SqlRepository sqlRepository;

    public WorkItemEntity findById(int id) {
        String sql = WorkItemSqlBuilder.buildFindById();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> WorkItemParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? WorkItemEntityMapper.map(rs) : null,
            id
        );
    }

    public List<WorkItemEntity> findAll() {
        String sql = WorkItemSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            ps -> WorkItemParameterBinder.bindFindAll(ps),
            WorkItemEntityMapper::map // ← ここで ResultSet を map
        );
    }

    public List<WorkItemEntity> findAllByCategoryId(int id) {
        String sql = WorkItemSqlBuilder.buildFindAllByCategoryId();

        return sqlRepository.findAll(
            sql,
            ps -> WorkItemParameterBinder.bindFindAllByCategoryId(ps, id),
            WorkItemEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public Integer deleteByIds(List<SimpleData> ids, String editor) {
        List<Integer> workItemIds = Utilities.createSequenceByIds(ids);
        String sql = WorkItemSqlBuilder.buildDeleteByIds(workItemIds.size());

        return sqlRepository.executeUpdate(
            sql,
            ps -> WorkItemParameterBinder.bindDeleteByIds(ps, workItemIds, editor)
        );
        // return result; // 成功件数。0なら削除なし
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public Integer insert(WorkItemEntity entity, String editor) {
        int index = 1;
        String sql = WorkItemSqlBuilder.buildInsert(index);
        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> WorkItemParameterBinder.bindInsert(pstmt, entity, editor, index),
            rs -> rs.next() ? rs.getInt("work_item_id") : null,
            entity
        );
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public Integer update(WorkItemEntity entity, String editor) {
        int index = 1;
        String sql = WorkItemSqlBuilder.buildUpdate(index);

        Integer result = sqlRepository.executeUpdate(
            sql,
            pstmt -> WorkItemParameterBinder.bindUpdate(pstmt, entity, editor, index)
        );

        return result; // 成功件数。0なら削除なし
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findParentCategoryCombo() {
        String sql = WorkItemSqlBuilder.buildFindParentCategoryCombo();

        return sqlRepository.findAll(
            sql,
            ps -> WorkItemParameterBinder.bindFindParentCategoryCombo(ps),
            SimpleDataMapper::map
        );
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<WorkItemEntity> downloadCsvByIds(List<SimpleData> ids) {
        List<Integer> workItemIds = Utilities.createSequenceByIds(ids);
        String sql = WorkItemSqlBuilder.buildDownloadCsvByIds(workItemIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> WorkItemParameterBinder.bindDownloadCsvByIds(ps, workItemIds),
            WorkItemEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
