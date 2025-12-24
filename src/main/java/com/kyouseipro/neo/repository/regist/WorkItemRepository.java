package com.kyouseipro.neo.repository.regist;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.regist.WorkItemEntity;
import com.kyouseipro.neo.mapper.data.SimpleDataMapper;
import com.kyouseipro.neo.mapper.regist.WorkItemEntityMapper;
import com.kyouseipro.neo.query.parameter.regist.WorkItemParameterBinder;
import com.kyouseipro.neo.query.sql.regist.WorkItemSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkItemRepository {
    private final SqlRepository sqlRepository;

    public WorkItemEntity findById(int id) {
        String sql = WorkItemSqlBuilder.buildFindByIdSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> WorkItemParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? WorkItemEntityMapper.map(rs) : null,
            id
        );
    }

    public List<WorkItemEntity> findAll() {
        String sql = WorkItemSqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> WorkItemParameterBinder.bindFindAll(ps),
            WorkItemEntityMapper::map // ← ここで ResultSet を map
        );
    }

    public List<WorkItemEntity> findAllByCategoryId(int id) {
        String sql = WorkItemSqlBuilder.buildFindAllByCategoryIdSql();

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
    public Integer deleteWorkItemByIds(List<SimpleData> ids, String editor) {
        List<Integer> workItemIds = Utilities.createSequenceByIds(ids);
        String sql = WorkItemSqlBuilder.buildDeleteWorkItemForIdsSql(workItemIds.size());

        return sqlRepository.executeUpdate(
            sql,
            ps -> WorkItemParameterBinder.bindDeleteForIds(ps, workItemIds, editor)
        );
        // return result; // 成功件数。0なら削除なし
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public Integer insertWorkItem(WorkItemEntity entity, String editor) {
        int index = 1;
        String sql = WorkItemSqlBuilder.buildInsertWorkItemSql(index);
        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> WorkItemParameterBinder.bindInsertWorkItemParameters(pstmt, entity, editor, index),
            rs -> rs.next() ? rs.getInt("work_item_id") : null,
            entity
        );
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public Integer updateWorkItem(WorkItemEntity entity, String editor) {
        int index = 1;
        String sql = WorkItemSqlBuilder.buildUpdateWorkItemSql(index);

        Integer result = sqlRepository.executeUpdate(
            sql,
            pstmt -> WorkItemParameterBinder.bindUpdateWorkItemParameters(pstmt, entity, editor, index)
        );

        return result; // 成功件数。0なら削除なし
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findParentCategoryCombo() {
        String sql = WorkItemSqlBuilder.buildFindParentCategoryComboSql();

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
    public List<WorkItemEntity> downloadCsvWorkItemByIds(List<SimpleData> ids) {
        List<Integer> workItemIds = Utilities.createSequenceByIds(ids);
        String sql = WorkItemSqlBuilder.buildDownloadCsvWorkItemForIdsSql(workItemIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> WorkItemParameterBinder.bindDownloadCsvForIds(ps, workItemIds),
            WorkItemEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
