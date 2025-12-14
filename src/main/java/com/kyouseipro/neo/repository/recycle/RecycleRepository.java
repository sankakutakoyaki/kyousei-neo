package com.kyouseipro.neo.repository.recycle;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleDateEntity;
import com.kyouseipro.neo.entity.recycle.RecycleEntity;
import com.kyouseipro.neo.mapper.data.SimpleDataMapper;
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
        String sql = RecycleSqlBuilder.buildFindByIdSql();

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
        String sql = RecycleSqlBuilder.buildFindByNumberSql();
        
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
    public RecycleEntity existsRecycleByNumber(String str) {
        // String sql = "";
        // switch (col) {
        //     case "regist":
        //         sql = RecycleSqlBuilder.buildExistsByNumberSql();
        //         break;
        //     case "delivery":
        //         sql = RecycleSqlBuilder.buildExistsByDeliveryNumberSql();
        //         break;
        //     case "shipping":
        //         sql = RecycleSqlBuilder.buildExistsByShippingNumberSql();
        //         break;
        //     case "loss":
        //         sql = RecycleSqlBuilder.buildExistsByLossNumberSql();
        //         break;        
        //     default:
        //         break;
        // }
        String sql = RecycleSqlBuilder.buildExistsByNumberSql();
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
    public List<RecycleEntity> findByEntityFromBetweenDate(LocalDate start, LocalDate end, String col) {
        if ("regist".equals(col)) { col = "update"; }
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
    public Integer saveRecycleList(List<RecycleEntity> itemList, String editor) {
        String sql = "";
        int index = 1;

        for (RecycleEntity entity : itemList) {
            if (entity.getState() == Enums.state.DELETE.getCode()) {
                sql += RecycleSqlBuilder.buildDeleteRecycleSql(index++);
            } else {
                if (entity.getRecycle_id() > 0) {
                    sql += RecycleSqlBuilder.buildUpdateRecycleSql(index++);
                } else {
                    sql += RecycleSqlBuilder.buildInsertRecycleSql(index++);
                }
            }
        }

        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> RecycleParameterBinder.bindSaveRecycleListParameters(pstmt, itemList, editor),
            rs -> rs.next() ? rs.getInt("recycle_id") : null,
            itemList
        );
    }

    /**
     * 日付の更新
     * @param itemList
     * @param editor
     * @param type
     * @return
     */
    public Integer updateRecycleDateList(List<RecycleDateEntity> itemList, String editor, String type) {
        String sql = "";
        int index = 1;

        for (int i = 0; i < itemList.size(); i++) {
            sql += RecycleSqlBuilder.buildUpdateRecycleDateSql(index++, type);
        }

        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> RecycleParameterBinder.bindUpdateRecycleDateListParameters(pstmt, itemList, editor),
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
    public Integer deleteRecycleByIds(List<SimpleData> ids, String editor) {
        List<Integer> recycleIds = Utilities.createSequenceByIds(ids);
        String sql = RecycleSqlBuilder.buildDeleteRecycleForIdsSql(recycleIds.size());

        return sqlRepository.executeUpdate(
            sql,
            ps -> RecycleParameterBinder.bindDeleteForIds(ps, recycleIds, editor)
        );
        // return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<RecycleEntity> downloadCsvRecycleByIds(List<SimpleData> ids, String editor) {
        List<Integer> recycleIds = Utilities.createSequenceByIds(ids);
        String sql = RecycleSqlBuilder.buildDownloadCsvRecycleForIdsSql(recycleIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> RecycleParameterBinder.bindDownloadCsvForIds(ps, recycleIds),
            RecycleEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
