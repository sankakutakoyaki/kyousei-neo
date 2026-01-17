package com.kyouseipro.neo.repository.recycle;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.dto.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleDateEntity;
import com.kyouseipro.neo.entity.recycle.RecycleEntity;
import com.kyouseipro.neo.mapper.dto.SimpleDataMapper;
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

    /**
     * Numberによる存在確認。
     * @param number
     * @return。
     */
    public Optional<RecycleEntity> existsByNumber(String number) {
        String sql = RecycleSqlBuilder.buildExistsByNumber();
        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> RecycleParameterBinder.bindExistsByNumber(ps, en),
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
    public List<RecycleEntity> findByBetween(LocalDate start, LocalDate end, String col) {
        // if ("regist".equals(col)) { col = "update"; }
        String sql = RecycleSqlBuilder.buildFindByBetween(col);
        
        return sqlRepository.findAll(
            sql,
            (ps, v) -> RecycleParameterBinder.bindFindByBetween(ps, start, end),
            RecycleEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param list
     * @param editor
     * @return 新規IDを返す。
     */
    public int save(List<RecycleEntity> list, String editor) {
        // String sql = "";
        // int index = 1;

        // for (RecycleEntity entity : list) {
        //     if (entity.getState() == Enums.state.DELETE.getCode()) {
        //         sql += RecycleSqlBuilder.buildDelete(index++);
        //     } else {
        //         if (entity.getRecycle_id() > 0) {
        //             sql += RecycleSqlBuilder.buildUpdate(index++);
        //         } else {
        //             sql += RecycleSqlBuilder.buildInsert(index++);
        //         }
        //     }
        // }

        // return sqlRepository.execute(
        //     sql,
        //     (pstmt, emp) -> RecycleParameterBinder.bindSave(pstmt, itemList, editor),
        //     rs -> rs.next() ? rs.getInt("recycle_id") : null,
        //     list
        // );
        StringBuilder sql = new StringBuilder();
        int index = 1;

        for (RecycleEntity entity : list) {
            if (entity.getState() == Enums.state.DELETE.getCode()) {
                sql.append(RecycleSqlBuilder.buildDelete(index++));
            } else if (entity.getRecycle_id() > 0) {
                sql.append(RecycleSqlBuilder.buildUpdate(index++));
            } else {
                sql.append(RecycleSqlBuilder.buildInsert(index++));
            }
        }

        try {
            return sqlRepository.executeRequired(
                sql.toString(),
                (ps, en) -> RecycleParameterBinder.bindSave(ps, list, editor),
                rs -> 1,   // ← IDは見ない
                list
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
    public int update(RecycleEntity entity, String editor) {
        String sql = RecycleSqlBuilder.buildUpdate(1);

        // return sqlRepository.execute(
        //     sql,
        //     (pstmt, emp) -> RecycleParameterBinder.bindUpdate(pstmt, entity, editor, index),
        //     rs -> rs.next() ? rs.getInt("recycle_id") : null,
        //     entity
        // );
        try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> RecycleParameterBinder.bindUpdate(ps, entity, editor, 1)
            );

            if (count == 0) {
                throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
            }

            return count;

        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("この番号はすでに使用されています。");
            }
            throw e;
        }
    }

    /**
     * 日付の更新
     * @param itemList
     * @param editor
     * @param type
     * @return
     */
    public int updateForDate(List<RecycleDateEntity> itemList, String editor, String type) {
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
        StringBuilder sql = new StringBuilder();
        int index = 1;

        for (RecycleDateEntity entity : itemList) {
            if (entity.getRecycle_id() == 0) {
                sql.append(RecycleSqlBuilder.buildInsertForDate(index++));
            } else {
                sql.append(RecycleSqlBuilder.buildUpdateForDate(index++, type));
            }
        }

        // try {
            return sqlRepository.executeUpdate(
                sql.toString(),
                ps -> RecycleParameterBinder.bindUpdateForDate(ps, itemList, editor)
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
        // List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = RecycleSqlBuilder.buildDeleteByIds(list.getIds().size());

        // return sqlRepository.executeUpdate(
        //     sql,
        //     ps -> RecycleParameterBinder.bindDeleteByIds(ps, recycleIds, editor)
        // );
        // // return result; // 成功件数。0なら削除なし
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
        // List<Integer> recycleIds = Utilities.createSequenceByIds(ids);
        // String sql = RecycleSqlBuilder.buildDownloadCsvByIds(recycleIds.size());

        // return sqlRepository.findAll(
        //     sql,
        //     ps -> RecycleParameterBinder.bindDownloadCsvByIds(ps, recycleIds),
        //     RecycleEntityMapper::map // ← ここで ResultSet を map
        // );
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        // List<Integer> ids = Utilities.createSequenceByIds(list);
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
        String sql = RecycleSqlBuilder.buildFindGroupCombo();
        
        return sqlRepository.findAll(
            sql,
            (ps, v) -> RecycleParameterBinder.bindFindGroupCombo(ps, null),
            SimpleDataMapper::map // ← ここで ResultSet を map
        );
    }
}
