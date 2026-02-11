package com.kyouseipro.neo.recycle.regist.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    public Optional<RecycleEntity> findById(int id) {
        String sql = RecycleSqlBuilder.buildFindById();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> RecycleParameterBinder.bindFindById(ps, en),
            rs -> rs.next() ? RecycleEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * お問合せ管理票番号でアイテムを抽出
     * @param id
     * @param editor
     * @return
     */
    public Optional<RecycleEntity> findByNumber(String number) {
        String sql = RecycleSqlBuilder.buildFindByNumber();
        
        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> RecycleParameterBinder.bindFindByNumber(ps, en),
            rs -> rs.next() ? RecycleEntityMapper.map(rs) : null,
            number
        );
    }

    // /**
    //  * Numberによる存在確認。
    //  * @param number
    //  * @return。
    //  */
    // public Optional<RecycleEntity> existsByNumber(String number) {
    //     String sql = RecycleSqlBuilder.buildExistsByNumber();
    //     return sqlRepository.executeQuery(
    //         sql,
    //         (ps, en) -> RecycleParameterBinder.bindExistsByNumber(ps, en),
    //         rs -> rs.next() ? RecycleEntityMapper.map(rs) : null,
    //         number
    //     );
    // }

    /**
     * 期間内のアイテムリストを取得
     * @param start
     * @param end
     * @return
     */
    public List<RecycleEntity> findByBetween(LocalDate start, LocalDate end, String col) {
        String sql = RecycleSqlBuilder.buildFindByBetween(col);
        
        return sqlRepository.findAll(
            sql,
            (ps, v) -> RecycleParameterBinder.bindFindByBetween(ps, start, end),
            RecycleEntityMapper::map // ← ここで ResultSet を map
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
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> RecycleParameterBinder.bindInsert(ps, entity, editor, 1),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("recycle_id");

                    if (rs.next()) {
                        throw new IllegalStateException("ID取得結果が複数行です");
                    }
                    return id;
                },
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
            int id = sqlRepository.executeRequired(
                sql,
                (ps, en) -> RecycleParameterBinder.bindUpdate(ps, entity, type, editor, 1),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    return rs.getInt("recycle_id");
                },
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

        return sqlRepository.executeUpdate(
            sql,
            ps -> RecycleParameterBinder.bindBulkUpdate(ps, req, 1)
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
        // String sql = "";
        // int index = 1;

        // for (RecycleDateEntity entity : itemList) {
        //     if (entity.getRecycle_id() == 0) {
        //         sql += RecycleSqlBuilder.buildInsertForDate(index++);
        //     } else {
        //         sql += RecycleSqlBuilder.buildUpdateForDate(index++, type);
        //     }
        // }

        // return sqlRepository.execute(
        //     sql,
        //     (pstmt, emp) -> RecycleParameterBinder.bindUpdateForDate(pstmt, itemList, editor),
        //     rs -> rs.next() ? rs.getInt("recycle_id") : null,
        //     itemList
        // );
        // StringBuilder sql = new StringBuilder();
        // int index = 1;

        String sql = RecycleSqlBuilder.buildUpdateForDate(1, type);
        // for (RecycleDateEntity entity : itemList) {
        //     if (entity.getRecycleId() == 0) {
        //         sql.append(RecycleSqlBuilder.buildInsertForDate(index++));
        //     } else {
        //         sql.append(RecycleSqlBuilder.buildUpdateForDate(index++, type));
        //     }
        // }

        // try {
        return sqlRepository.executeUpdate(
            sql,
            ps -> RecycleParameterBinder.bindUpdateForDate(ps, entity, editor)
        );
        // } catch (RuntimeException e) {
        //     if (SqlExceptionUtil.isDuplicateKey(e)) {
        //         throw new BusinessException("この日付はすでに登録されています。");
        //     }
        //     throw e;
        // }
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(IdListRequest list, String editor) {
        String sql = RecycleSqlBuilder.buildDeleteByIds(list.getIds().size());
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> RecycleParameterBinder.bindDeleteByIds(ps, list.getIds(), editor)
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
    public List<RecycleEntity> downloadCsvByIds(IdListRequest list, String editor) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        String sql = RecycleSqlBuilder.buildDownloadCsvByIds(list.getIds().size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> RecycleParameterBinder.bindDownloadCsvByIds(ps, list.getIds()),
            RecycleEntityMapper::map
        );
    }

    /**
     * グループコンボボックス用のリストを取得する
     * @return
     */
    public List<SimpleData> findGroupCombo() {
        String sql = "SELECT recycle_group_id as number, name as text FROM recycle_groups WHERE NOT (state = ?)";
        
        return sqlRepository.findAll(
            sql,
            (ps, v) -> ps.setInt(1, Enums.state.DELETE.getCode()),
            SimpleDataMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * リサイクルメーカーコンボボックス用のリストを取得する
     * @return
     */
    public List<ComboData> findRecycleMakerCombo() {
        String sql = "SELECT recycle_maker_id as id, code as number, name as text FROM recycle_makers WHERE NOT (state = ?)";
        
        return sqlRepository.findAll(
            sql,
            (ps, v) -> ps.setInt(1, Enums.state.DELETE.getCode()),
            ComboDataMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * リサイクル品目コンボボックス用のリストを取得する
     * @return
     */
    public List<ComboData> findRecycleItemCombo() {
        String sql = "SELECT recycle_item_id as id, code as number, name as text FROM recycle_items WHERE NOT (state = ?)";
        
        return sqlRepository.findAll(
            sql,
            (ps, v) -> ps.setInt(1, Enums.state.DELETE.getCode()),
            ComboDataMapper::map // ← ここで ResultSet を map
        );
    }
}
