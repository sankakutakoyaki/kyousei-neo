package com.kyouseipro.neo.repository.work;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.dto.IdListRequest;
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

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public Optional<WorkItemEntity> findById(int id) {
        String sql = WorkItemSqlBuilder.buildFindById();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> WorkItemParameterBinder.bindFindById(ps, en),
            rs -> rs.next() ? WorkItemEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<WorkItemEntity> findAll() {
        String sql = WorkItemSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> WorkItemParameterBinder.bindFindAll(ps),
            WorkItemEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 全件取得（CategoryIDで指定）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<WorkItemEntity> findAllByCategoryId(int id) {
        String sql = WorkItemSqlBuilder.buildFindAllByCategoryId();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> WorkItemParameterBinder.bindFindAllByCategoryId(ps, id),
            WorkItemEntityMapper::map // ← ここで ResultSet を map
        );
    }
    
    /**
     * コンボボックス用リスト取得
     * @return
     */
    public List<SimpleData> findParentCategoryCombo() {
        String sql = WorkItemSqlBuilder.buildFindParentCategoryCombo();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> WorkItemParameterBinder.bindFindParentCategoryCombo(ps),
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

        // return sqlRepository.executeQuery(
        //     sql,
        //     (ps, en) -> WorkItemParameterBinder.bindInsert(ps, en, editor, 1),
        //     rs -> rs.next() ? rs.getInt("work_item_id") : null,
        //     entity
        // );
        try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> WorkItemParameterBinder.bindInsert(ps, en, editor, 1),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("work_item_id");

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

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public int update(WorkItemEntity entity, String editor) {
        String sql = WorkItemSqlBuilder.buildUpdate(1);

        // Integer result = sqlRepository.executeUpdate(
        //     sql,
        //     pstmt -> WorkItemParameterBinder.bindUpdate(pstmt, entity, editor, index)
        // );

        // return result; // 成功件数。0なら削除なし
        try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> WorkItemParameterBinder.bindUpdate(ps, entity, editor, 1)
            );

            if (count == 0) {
                throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
            }

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
        // List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = WorkItemSqlBuilder.buildDeleteByIds(list.getIds().size());

        // return sqlRepository.executeUpdate(
        //     sql,
        //     ps -> WorkItemParameterBinder.bindDeleteByIds(ps, workItemIds, editor)
        // );
        // return result; // 成功件数。0なら削除なし
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> WorkItemParameterBinder.bindDeleteByIds(ps, list.getIds(), editor)
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param list
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<WorkItemEntity> downloadCsvByIds(IdListRequest list, String editor) {
        // List<Integer> workItemIds = Utilities.createSequenceByIds(ids);
        // String sql = WorkItemSqlBuilder.buildDownloadCsvByIds(workItemIds.size());

        // return sqlRepository.findAll(
        //     sql,
        //     ps -> WorkItemParameterBinder.bindDownloadCsvByIds(ps, workItemIds),
        //     WorkItemEntityMapper::map // ← ここで ResultSet を map
        // );
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        // List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = WorkItemSqlBuilder.buildDownloadCsvByIds(list.getIds().size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> WorkItemParameterBinder.bindDownloadCsvByIds(ps, list.getIds()),
            WorkItemEntityMapper::map
        );
    }
}
