package com.kyouseipro.neo.repository.recycle;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
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
        String sql = RecycleMakerSqlBuilder.buildFindByIdSql();

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
        String sql = RecycleMakerSqlBuilder.buildFindByCodeSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> RecycleMakerParameterBinder.bindFindByCode(pstmt, comp),
            rs -> rs.next() ? RecycleMakerEntityMapper.map(rs) : null,
            code
        );
    }

    /**
     * 削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public Integer deleteRecycleMakerByIds(List<SimpleData> ids) {
        List<Integer> recycleMakerIds = Utilities.createSequenceByIds(ids);
        String sql = RecycleMakerSqlBuilder.buildDeleteRecycleMakerForIdsSql(recycleMakerIds.size());

        return sqlRepository.executeUpdate(
            sql,
            ps -> RecycleMakerParameterBinder.bindDeleteForIds(ps, recycleMakerIds)
        );
        // return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<RecycleMakerEntity> downloadCsvRecycleMakerByIds(List<SimpleData> ids) {
        List<Integer> recycleMakerIds = Utilities.createSequenceByIds(ids);
        String sql = RecycleMakerSqlBuilder.buildDownloadCsvRecycleMakerForIdsSql(recycleMakerIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> RecycleMakerParameterBinder.bindDownloadCsvForIds(ps, recycleMakerIds),
            RecycleMakerEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public Integer insertRecycleMaker(RecycleMakerEntity entity) {
        String sql = RecycleMakerSqlBuilder.buildInsertRecycleMakerSql();

        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> RecycleMakerParameterBinder.bindInsertRecycleMakerParameters(pstmt, emp),
            rs -> rs.next() ? rs.getInt("recycle_maker_id") : null,
            entity
        );
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public Integer updateRecycleMaker(RecycleMakerEntity entity) {
        String sql = RecycleMakerSqlBuilder.buildUpdateRecycleMakerSql();

        Integer result = sqlRepository.executeUpdate(
            sql,
            pstmt -> RecycleMakerParameterBinder.bindUpdateRecycleMakerParameters(pstmt, entity)
        );

        return result; // 成功件数。0なら削除なし
    }
}
