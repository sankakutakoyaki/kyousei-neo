package com.kyouseipro.neo.repository.recycle;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleDateEntity;
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
    public RecycleEntity findById(int recycleId) {
        String sql = RecycleSqlBuilder.buildFindById();

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
    public RecycleEntity findByNumber(String number) {
        String sql = RecycleSqlBuilder.buildFindByNumber();
        
        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> RecycleParameterBinder.bindFindByNumber(pstmt, comp),
            rs -> rs.next() ? RecycleEntityMapper.map(rs) : null,
            number
        );
    }

    /**
     * Numberによる存在確認。
     * @param number
     * @return。
     */
    public RecycleEntity existsByNumber(String str) {
        String sql = RecycleSqlBuilder.buildExistsByNumber();
        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> RecycleParameterBinder.bindExistsByNumber(pstmt, comp),
            rs -> rs.next() ? RecycleEntityMapper.map(rs) : null,
            str
        );
    }

    /**
     * 期間内のアイテムリストを取得
     * @param start
     * @param end
     * @return
     */
    public List<RecycleEntity> findByBetween(LocalDate start, LocalDate end, String col) {
        if ("regist".equals(col)) { col = "update"; }
        String sql = RecycleSqlBuilder.buildFindByBetween(col);
        
        return sqlRepository.findAll(
            sql,
            ps -> RecycleParameterBinder.bindFindByBetween(ps, start, end),
            RecycleEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param itemEntity
     * @param editor
     * @return 新規IDを返す。
     */
    public Integer save(List<RecycleEntity> itemList, String editor) {
        String sql = "";
        int index = 1;

        for (RecycleEntity entity : itemList) {
            if (entity.getState() == Enums.state.DELETE.getCode()) {
                sql += RecycleSqlBuilder.buildDelete(index++);
            } else {
                if (entity.getRecycle_id() > 0) {
                    sql += RecycleSqlBuilder.buildUpdate(index++);
                } else {
                    sql += RecycleSqlBuilder.buildInsert(index++);
                }
            }
        }

        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> RecycleParameterBinder.bindSave(pstmt, itemList, editor),
            rs -> rs.next() ? rs.getInt("recycle_id") : null,
            itemList
        );
    }

    /**
     * 更新。
     * @param entity
     * @param editor
     * @return 新規IDを返す。
     */
    public Integer update(RecycleEntity entity, String editor) {
        int index = 1;
        String sql = RecycleSqlBuilder.buildUpdate(index);

        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> RecycleParameterBinder.bindUpdate(pstmt, entity, editor, index),
            rs -> rs.next() ? rs.getInt("recycle_id") : null,
            entity
        );
    }

    /**
     * 日付の更新
     * @param itemList
     * @param editor
     * @param type
     * @return
     */
    public Integer updateForDate(List<RecycleDateEntity> itemList, String editor, String type) {
        String sql = "";
        int index = 1;

        for (RecycleDateEntity entity : itemList) {
            if (entity.getRecycle_id() == 0) {
                sql += RecycleSqlBuilder.buildInsertForDate(index++);
            } else {
                sql += RecycleSqlBuilder.buildUpdateForDate(index++, type);
            }
        }

        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> RecycleParameterBinder.bindUpdateForDate(pstmt, itemList, editor),
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
    public Integer deleteByIds(List<SimpleData> ids, String editor) {
        List<Integer> recycleIds = Utilities.createSequenceByIds(ids);
        String sql = RecycleSqlBuilder.buildDeleteByIds(recycleIds.size());

        return sqlRepository.executeUpdate(
            sql,
            ps -> RecycleParameterBinder.bindDeleteByIds(ps, recycleIds, editor)
        );
        // return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<RecycleEntity> downloadCsvByIds(List<SimpleData> ids, String editor) {
        List<Integer> recycleIds = Utilities.createSequenceByIds(ids);
        String sql = RecycleSqlBuilder.buildDownloadCsvByIds(recycleIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> RecycleParameterBinder.bindDownloadCsvByIds(ps, recycleIds),
            RecycleEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
