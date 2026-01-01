package com.kyouseipro.neo.repository.recycle;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleMakerEntity;
import com.kyouseipro.neo.mapper.recycle.RecycleMakerEntityMapper;
import com.kyouseipro.neo.query.parameter.recycle.RecycleMakerParameterBinder;
import com.kyouseipro.neo.query.sql.recycle.RecycleMakerSqlBuilder;
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
    // public RecycleMakerEntity findById(int recycleMakerId) {
    //     String sql = RecycleMakerSqlBuilder.buildFindById();

    //     return sqlRepository.execute(
    //         sql,
    //         (pstmt, comp) -> RecycleMakerParameterBinder.bindFindById(pstmt, comp),
    //         rs -> rs.next() ? RecycleMakerEntityMapper.map(rs) : null,
    //         recycleMakerId
    //     );
    // }
    public Optional<RecycleMakerEntity> findById(int id) {
        String sql = RecycleMakerSqlBuilder.buildFindById();
        
        return sqlRepository.executeQuery(
            sql,
            (pstmt, e) -> RecycleMakerParameterBinder.bindFindById(pstmt, e),
            rs -> rs.next() ? RecycleMakerEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * Codeによる取得。
     * @param code
     * @return IDから取得したEntityをかえす。
     */
    public Optional<RecycleMakerEntity> findByCode(int code) {
        String sql = RecycleMakerSqlBuilder.buildFindByCode();

        return sqlRepository.executeQuery(
            sql,
            (pstmt, comp) -> RecycleMakerParameterBinder.bindFindByCode(pstmt, comp),
            rs -> rs.next() ? RecycleMakerEntityMapper.map(rs) : null,
            code
        );
    }

    /**
     * RecycleMaker 全件取得。
     * 0件の場合は空リストを返す。
     * @return
     */
    // public List<RecycleMakerEntity> findAll() {
    //     String sql = RecycleMakerSqlBuilder.buildFindAll();

    //     return sqlRepository.findAll(
    //         sql,
    //         ps -> RecycleMakerParameterBinder.bindFindAll(ps, null),
    //         RecycleMakerEntityMapper::map // ← ここで ResultSet を map
    //     );
    // }
    public List<RecycleMakerEntity> findAll() {
        String sql = RecycleMakerSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> RecycleMakerParameterBinder.bindFindAll(ps, null),
            RecycleMakerEntityMapper::map
        );
    }

    /**
     * 削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    // public Integer deleteByIds(List<SimpleData> ids) {
    //     List<Integer> recycleMakerIds = Utilities.createSequenceByIds(ids);
    //     String sql = RecycleMakerSqlBuilder.buildDeleteByIds(recycleMakerIds.size());

    //     return sqlRepository.executeUpdate(
    //         sql,
    //         ps -> RecycleMakerParameterBinder.bindDeleteByIds(ps, recycleMakerIds)
    //     );
    //     // return result; // 成功件数。0なら削除なし
    // }
    public int deleteByIds(List<SimpleData> list) {
        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = RecycleMakerSqlBuilder.buildDeleteByIds(ids.size());

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> RecycleMakerParameterBinder.bindDeleteByIds(ps, ids)
        );
        if (count == 0) {
            throw new BusinessException("削除対象が存在しません");
        }

        return count;
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    // public List<RecycleMakerEntity> downloadCsvByIds(List<SimpleData> ids) {
    //     List<Integer> recycleMakerIds = Utilities.createSequenceByIds(ids);
    //     String sql = RecycleMakerSqlBuilder.buildDownloadCsvByIds(recycleMakerIds.size());

    //     return sqlRepository.findAll(
    //         sql,
    //         ps -> RecycleMakerParameterBinder.bindDownloadCsvByIds(ps, recycleMakerIds),
    //         RecycleMakerEntityMapper::map // ← ここで ResultSet を map
    //     );
    // }
    public List<RecycleMakerEntity> downloadCsvByIds(List<SimpleData> ids) {

        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        List<Integer> recycleMakerIds = Utilities.createSequenceByIds(ids);
        String sql = RecycleMakerSqlBuilder.buildDownloadCsvByIds(recycleMakerIds.size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> RecycleMakerParameterBinder.bindDownloadCsvByIds(ps, recycleMakerIds),
            RecycleMakerEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    // public Integer insert(RecycleMakerEntity entity) {
    //     String sql = RecycleMakerSqlBuilder.buildInsert();

    //     try {
    //         return sqlRepository.execute(
    //             sql,
    //             (pstmt, emp) -> RecycleMakerParameterBinder.bindInsert(pstmt, emp),
    //             rs -> rs.next() ? rs.getInt("recycle_maker_id") : null,
    //             entity
    //         );

    //     } catch (RuntimeException e) {
    //         if (SqlExceptionUtil.isDuplicateKey(e)) {
    //             throw new BusinessException("このコードはすでに使用されています。");
    //         }
    //         throw e;
    //     }
    // }
    public int insert(RecycleMakerEntity entity) {
        String sql = RecycleMakerSqlBuilder.buildInsert();

        try {
            return sqlRepository.executeRequired(
                sql,
                (pstmt, emp) -> RecycleMakerParameterBinder.bindInsert(pstmt, emp),
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
    // public Integer update(RecycleMakerEntity entity) {
    //     try {
    //         return sqlRepository.executeUpdate(
    //             sql,
    //             pstmt -> RecycleMakerParameterBinder.bindUpdate(pstmt, entity)
    //         );

    //     } catch (RuntimeException e) {
    //         if (SqlExceptionUtil.isDuplicateKey(e)) {
    //             throw new BusinessException("このコードはすでに使用されています。");
    //         }
    //         throw e;
    //     }
    // }
    public int update(RecycleMakerEntity entity) {
        String sql = RecycleMakerSqlBuilder.buildUpdate();

        try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> RecycleMakerParameterBinder.bindUpdate(ps, entity)
            );

            if (count == 0) {
                throw new BusinessException("更新対象が存在しません");
            }

            return count;

        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("このコードはすでに使用されています。");
            }
            throw e;
        }
    }
}
