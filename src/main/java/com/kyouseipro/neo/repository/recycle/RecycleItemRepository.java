package com.kyouseipro.neo.repository.recycle;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
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
    public Optional<RecycleItemEntity> findById(int id) {
        String sql = RecycleItemSqlBuilder.buildFindById();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> RecycleItemParameterBinder.bindFindById(ps, en),
            rs -> rs.next() ? RecycleItemEntityMapper.map(rs) : null,
            id
        );
    } 

    /**
     * Codeによる取得。
     * @param code
     * @return IDから取得したEntityをかえす。
     */
    public Optional<RecycleItemEntity> findByCode(int code) {
        String sql = RecycleItemSqlBuilder.buildFindByCode();
        
        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> RecycleItemParameterBinder.bindFindByCode(ps, en),
            rs -> rs.next() ? RecycleItemEntityMapper.map(rs) : null,
            code
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(RecycleItemEntity entity) {
        String sql = RecycleItemSqlBuilder.buildInsert();
        // return sqlRepository.execute(
        //     sql,
        //     (pstmt, emp) -> RecycleItemParameterBinder.bindInsert(pstmt, emp),
        //     rs -> rs.next() ? rs.getInt("recycle_maker_id") : null,
        //     entity
        // );
        try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> RecycleItemParameterBinder.bindInsert(ps, en),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("recycle_maker_id");

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
    public int update(RecycleItemEntity entity) {
        String sql = RecycleItemSqlBuilder.buildUpdate();

        // Integer result = sqlRepository.executeUpdate(
        //     sql,
        //     pstmt -> RecycleItemParameterBinder.bindUpdate(pstmt, entity)
        // );

        // return result; // 成功件数。0なら削除なし
        try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> RecycleItemParameterBinder.bindUpdate(ps, entity)
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
     * 削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(List<SimpleData> list) {
        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = RecycleItemSqlBuilder.buildDeleteByIds(ids.size());

        // return sqlRepository.executeUpdate(
        //     sql,
        //     ps -> RecycleItemParameterBinder.bindDeleteForIds(ps, recycleItemIds)
        // );
        // return result; // 成功件数。0なら削除なし
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> RecycleItemParameterBinder.bindDeleteForIds(ps, ids)
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<RecycleItemEntity> downloadCsvByIds(List<SimpleData> list) {
        // List<Integer> recycleItemIds = Utilities.createSequenceByIds(ids);
        // String sql = RecycleItemSqlBuilder.buildDownloadCsvByIds(recycleItemIds.size());

        // return sqlRepository.findAll(
        //     sql,
        //     ps -> RecycleItemParameterBinder.bindDownloadCsvForIds(ps, recycleItemIds),
        //     RecycleItemEntityMapper::map // ← ここで ResultSet を map
        // );
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = RecycleItemSqlBuilder.buildDownloadCsvByIds(ids.size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> RecycleItemParameterBinder.bindDownloadCsvForIds(ps, ids),
            RecycleItemEntityMapper::map
        );
    }
}