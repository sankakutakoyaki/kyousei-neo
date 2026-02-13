package com.kyouseipro.neo.recycle.regist.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.combo.entity.ComboData;
import com.kyouseipro.neo.common.combo.mapper.ComboDataMapper;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.common.simpledata.mapper.SimpleDataMapper;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.recycle.regist.entity.RecycleDateEntity;
import com.kyouseipro.neo.recycle.regist.entity.RecycleEntity;
import com.kyouseipro.neo.recycle.regist.entity.RecycleEntityRequest;
import com.kyouseipro.neo.recycle.regist.mapper.RecycleEntityMapper;

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
    public RecycleEntity findById(int id) {
        String sql = RecycleSqlBuilder.buildFindById();

        return sqlRepository.queryOne(
            sql,
            (ps, v) -> RecycleParameterBinder.bindFindById(ps, id),
            RecycleEntityMapper::map
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
        
        return sqlRepository.queryOne(
            sql,
            (ps, v) -> RecycleParameterBinder.bindFindByNumber(ps, number),
            RecycleEntityMapper::map
        );
    }

    /**
     * 期間内のアイテムリストを取得
     * @param start
     * @param end
     * @return
     */
    public List<RecycleEntity> findByBetween(LocalDate start, LocalDate end, String col) {
        String sql = RecycleSqlBuilder.buildFindByBetween(col);
        
        return sqlRepository.queryList(
            sql,
            (ps, v) -> RecycleParameterBinder.bindFindByBetween(ps, start, end),
            RecycleEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @param editor
     * @return 新規IDを返す。
     */
    public int save(RecycleEntity entity, String editor) {
        String sql = RecycleSqlBuilder.buildInsert(1);

        try {
            return sqlRepository.insert(
                sql,
                (ps, en) -> RecycleParameterBinder.bindInsert(ps, en, editor, 1),
                rs -> rs.getInt("recycle_id"),
                entity
            );
        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("この番号はすでに使用されています。");
            }
            throw e;
        }
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public int update(RecycleEntity entity, String type, String editor) {
        String sql = RecycleSqlBuilder.buildUpdate(1, type);
        try {
            int id = sqlRepository.update(
                sql,
                (ps, en) -> RecycleParameterBinder.bindUpdate(ps, en, type, editor, 1),
                entity
            );

            if (id == 0) {
                switch (type) {
                    case "shipping":
                        throw new BusinessException("この番号は登録または引渡されていません。");
                    default:
                        throw new BusinessException("この番号は使用登録されていません。");
                }
            }

            return id;

        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("この番号はすでに使用されています。");
            }
            throw e;
        }
    }

    /**
     * 変更箇所のみを更新する
     * @param req
     * @return
     */
    public int bulkUpdate(RecycleEntityRequest req) {

        String sql = RecycleSqlBuilder.buildBulkUpdate(req);

        return sqlRepository.updateRequired(
            sql,
            (ps, v) -> RecycleParameterBinder.bindBulkUpdate(ps, req, 1)
        );
    }

    /**
     * 日付の更新
     * @param itemList
     * @param editor
     * @param type
     * @return
     */
    public int updateForDate(RecycleDateEntity entity, String editor, String type) {

        String sql = RecycleSqlBuilder.buildUpdateForDate(1, type);
        
        return sqlRepository.updateRequired(
            sql,
            (ps, e) -> RecycleParameterBinder.bindUpdateForDate(ps, e, editor),
            entity
        );
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
        
        String sql = RecycleSqlBuilder.buildDeleteByIds(list.getIds().size());
        int count = sqlRepository.updateRequired(
            sql,
            (ps, e) -> RecycleParameterBinder.bindDeleteByIds(ps, e.getIds(), editor),
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
    public List<RecycleEntity> downloadCsvByIds(IdListRequest list, String editor) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        String sql = RecycleSqlBuilder.buildDownloadCsvByIds(list.getIds().size());
        return sqlRepository.queryList(
            sql,
            (ps, e) -> RecycleParameterBinder.bindDownloadCsvByIds(ps, e.getIds()),
            RecycleEntityMapper::map,
            list
        );
    }

    /**
     * グループコンボボックス用のリストを取得する
     * @return
     */
    public List<SimpleData> findGroupCombo() {
        return sqlRepository.queryList(
            """
            SELECT recycle_group_id as number, name as text FROM recycle_groups WHERE NOT (state = ?)
            """,
            (ps, v) -> ps.setInt(1, Enums.state.DELETE.getCode()),
            SimpleDataMapper::map
        );
    }

    /**
     * リサイクルメーカーコンボボックス用のリストを取得する
     * @return
     */
    public List<ComboData> findRecycleMakerCombo() {
        return sqlRepository.queryList(
            """
            SELECT recycle_maker_id as id, code as number, name as text FROM recycle_makers WHERE NOT (state = ?)
            """,
            (ps, v) -> ps.setInt(1, Enums.state.DELETE.getCode()),
            ComboDataMapper::map
        );
    }

    /**
     * リサイクル品目コンボボックス用のリストを取得する
     * @return
     */
    public List<ComboData> findRecycleItemCombo() {
        return sqlRepository.queryList(
            """
            SELECT recycle_item_id as id, code as number, name as text FROM recycle_items WHERE NOT (state = ?)
            """,
            (ps, v) -> ps.setInt(1, Enums.state.DELETE.getCode()),
            ComboDataMapper::map 
        );
    }
}
