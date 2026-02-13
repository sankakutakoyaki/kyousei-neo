package com.kyouseipro.neo.work.item.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.common.simpledata.mapper.SimpleDataMapper;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.work.item.entity.WorkItemEntity;
import com.kyouseipro.neo.work.item.mapper.WorkItemEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkItemRepository {
    private final SqlRepository sqlRepository;

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public WorkItemEntity findById(int id) {
        String sql = WorkItemSqlBuilder.buildFindById();

        return sqlRepository.queryOne(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            WorkItemEntityMapper::map
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<WorkItemEntity> findAll() {
        String sql = WorkItemSqlBuilder.buildFindAll();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            WorkItemEntityMapper::map
        );
    }

    /**
     * 全件取得（CategoryIDで指定）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<WorkItemEntity> findAllByCategoryId(int id) {
        String sql = WorkItemSqlBuilder.buildFindAllByCategoryId();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            WorkItemEntityMapper::map
        );
    }
    
    /**
     * コンボボックス用リスト取得
     * @return
     */
    public List<SimpleData> findParentCategoryCombo() {
        String sql = WorkItemSqlBuilder.buildFindParentCategoryCombo();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> ps.setInt(1, Enums.state.DELETE.getCode()),
            SimpleDataMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(WorkItemEntity entity, String editor) {
        String sql = WorkItemSqlBuilder.buildInsert(1);

        try {
            return sqlRepository.insert(
                sql,
                (ps, en) -> WorkItemParameterBinder.bindInsert(ps, en, editor, 1),
                rs -> rs.getInt("work_item_id"),
                entity
            );
        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("このコードはすでに使用されています。");
            }
            throw e;
        }
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public int update(WorkItemEntity entity, String editor) {
        String sql = WorkItemSqlBuilder.buildUpdate(1);

        try {
            int count = sqlRepository.updateRequired(
                sql,
                (ps, e) -> WorkItemParameterBinder.bindUpdate(ps, e, editor, 1),
                entity
            );

            return count;
        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("このコードはすでに使用されています。");
            }
            throw e;
        }
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(IdListRequest list, String editor) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }
        
        String sql = WorkItemSqlBuilder.buildDeleteByIds(list.getIds().size());
        int count = sqlRepository.updateRequired(
            sql,
            (ps, e) -> WorkItemParameterBinder.bindDeleteByIds(ps, e.getIds(), editor),
            list
        );

        return count;
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param list
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<WorkItemEntity> downloadCsvByIds(IdListRequest list, String editor) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        String sql = WorkItemSqlBuilder.buildDownloadCsvByIds(list.getIds().size());
        return sqlRepository.queryList(
            sql,
            (ps, e) -> WorkItemParameterBinder.bindDownloadCsvByIds(ps, e.getIds()),
            WorkItemEntityMapper::map,
            list
        );
    }
}
