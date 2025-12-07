package com.kyouseipro.neo.repository.recycle;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.recycle.RecycleEntity;
import com.kyouseipro.neo.mapper.recycle.RecycleEntityMapper;
import com.kyouseipro.neo.query.parameter.recycle.RecycleParameterBinder;
import com.kyouseipro.neo.query.sql.recycle.RecycleSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecycleRepository {
    private final SqlRepository sqlRepository;
    
    /**
     * IDによる取得。
     * @param orderId
     * @return IDから取得したEntityをかえす。
     */
    public RecycleEntity findById(String sql, int recycleId) {
        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> RecycleParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? RecycleEntityMapper.map(rs) : null,
            recycleId
        );
    }

    /**
     * お問合せ管理票番号でアイテムを抽出
     * @param id
     * @param editor
     * @return
     */
    /**
     * IDによる取得。
     * @param orderId
     * @return IDから取得したEntityをかえす。
     */
    public RecycleEntity findByNumber(String sql, String number) {
        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> RecycleParameterBinder.bindFindByNumber(pstmt, comp),
            rs -> rs.next() ? RecycleEntityMapper.map(rs) : null,
            number
        );
    }

    /**
     * 期間内のアイテムリストを取得
     * @param start
     * @param end
     * @return
     */
    public List<RecycleEntity> findByEntityFromBetweenDate(LocalDate start, LocalDate end, String col) {
        String sql = RecycleSqlBuilder.buildFindByBetweenRecycleEntity(col);

        return sqlRepository.findAll(
            sql,
            ps -> RecycleParameterBinder.bindFindByBetweenRecycleEntity(ps, start, end),
            RecycleEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param itemEntity
     * @param editor
     * @return 新規IDを返す。
     */
    public Integer saveRecycleList(String sql, List<RecycleEntity> itemList, String editor) {

        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> RecycleParameterBinder.bindSaveRecycleListParameters(pstmt, itemList, editor),
            rs -> rs.next() ? rs.getInt("recycle_id") : null,
            itemList
        );
    }

    /**
     * 削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public Integer deleteRecycleByIds(String sql, List<Integer> ids, String editor) {

        return sqlRepository.executeUpdate(
            sql,
            ps -> RecycleParameterBinder.bindDeleteForIds(ps, ids, editor)
        );
        // return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<RecycleEntity> downloadCsvRecycleByIds(String sql, List<Integer> ids, String editor) {

        return sqlRepository.findAll(
            sql,
            ps -> RecycleParameterBinder.bindDownloadCsvForIds(ps, ids),
            RecycleEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
