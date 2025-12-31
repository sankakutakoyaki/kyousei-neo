package com.kyouseipro.neo.repository.recycle;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
import com.kyouseipro.neo.common.exception.SystemException;
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
    public RecycleMakerEntity findById(int recycleMakerId) {
        String sql = RecycleMakerSqlBuilder.buildFindById();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> RecycleMakerParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? RecycleMakerEntityMapper.map(rs) : null,
            recycleMakerId
        );
    }

    /**
     * Codeによる取得。
     * @param code
     * @return IDから取得したEntityをかえす。
     */
    public RecycleMakerEntity findByCode(int code) {
        String sql = RecycleMakerSqlBuilder.buildFindByCode();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> RecycleMakerParameterBinder.bindFindByCode(pstmt, comp),
            rs -> rs.next() ? RecycleMakerEntityMapper.map(rs) : null,
            code
        );
    }

    // 全件取得
    public List<RecycleMakerEntity> findAll() {
        String sql = RecycleMakerSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            ps -> RecycleMakerParameterBinder.bindFindAll(ps, null),
            RecycleMakerEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public Integer deleteByIds(List<SimpleData> ids) {
        List<Integer> recycleMakerIds = Utilities.createSequenceByIds(ids);
        String sql = RecycleMakerSqlBuilder.buildDeleteByIds(recycleMakerIds.size());

        return sqlRepository.executeUpdate(
            sql,
            ps -> RecycleMakerParameterBinder.bindDeleteByIds(ps, recycleMakerIds)
        );
        // return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<RecycleMakerEntity> downloadCsvByIds(List<SimpleData> ids) {
        List<Integer> recycleMakerIds = Utilities.createSequenceByIds(ids);
        String sql = RecycleMakerSqlBuilder.buildDownloadCsvByIds(recycleMakerIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> RecycleMakerParameterBinder.bindDownloadCsvByIds(ps, recycleMakerIds),
            RecycleMakerEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public Integer insert(RecycleMakerEntity entity) {
        String sql = RecycleMakerSqlBuilder.buildInsert();

        try {
            return sqlRepository.execute(
                sql,
                (pstmt, emp) -> RecycleMakerParameterBinder.bindInsert(pstmt, emp),
                rs -> rs.next() ? rs.getInt("recycle_maker_id") : null,
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
    public Integer update(RecycleMakerEntity entity) {
        String sql = RecycleMakerSqlBuilder.buildUpdate();

        try {
            return sqlRepository.executeUpdate(
                sql,
                pstmt -> RecycleMakerParameterBinder.bindUpdate(pstmt, entity)
            );

        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("このコードはすでに使用されています。");
            }
            throw e;
        }
    }
}
