package com.kyouseipro.neo.repository.corporation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
import com.kyouseipro.neo.entity.corporation.StaffEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.mapper.corporation.StaffEntityMapper;
import com.kyouseipro.neo.query.parameter.corporation.StaffParameterBinder;
import com.kyouseipro.neo.query.sql.corporation.StaffSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StaffRepository {
    private final SqlRepository sqlRepository;
   
    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public Optional<StaffEntity> findById(int id) {
        String sql = StaffSqlBuilder.buildFindById();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> StaffParameterBinder.bindFindById(ps, en),
            rs -> rs.next() ? StaffEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<StaffEntity> findAll() {
        String sql = StaffSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> StaffParameterBinder.bindFindAll(ps, null),
            StaffEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(StaffEntity entity, String editor) {
        String sql = StaffSqlBuilder.buildInsert();

        // return sqlRepository.executeRequired(
        //     sql,
        //     (ps, en) -> StaffParameterBinder.bindInsert(pstmt, en, editor),
        //     rs -> rs.next() ? rs.getInt("staff_id") : null,
        //     staff
        // );
        try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> StaffParameterBinder.bindInsert(ps, en, editor),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("staff_id");

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
    public int update(StaffEntity entity, String editor) {
        String sql = StaffSqlBuilder.buildUpdate();

        // return sqlRepository.execute(
        //     sql,
        //     (pstmt, entity) -> StaffParameterBinder.bindUpdate(pstmt, entity, editor),
        //     rs -> rs.next() ? rs.getInt("staff_id") : null,
        //     staff
        // );
        try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> StaffParameterBinder.bindUpdate(ps, entity, editor)
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

    /**
     * IDで指定したENTITYを論理削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(List<SimpleData> list, String editor) {
        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = StaffSqlBuilder.buildDeleteByIds(ids.size());

        // int result = sqlRepository.executeUpdate(
        //     sql,
        //     ps -> StaffParameterBinder.bindDeleteByIds(ps, staffIds, editor)
        // );

        // return result; // 成功件数。0なら削除なし
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> StaffParameterBinder.bindDeleteByIds(ps, ids, editor)
        );
        if (count == 0) {
            throw new BusinessException("削除対象が存在しません");
        }

        return count;
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<StaffEntity> downloadCsvByIds(List<SimpleData> list, String editor) {
        // List<Integer> staffIds = Utilities.createSequenceByIds(ids);
        // String sql = StaffSqlBuilder.buildDownloadCsvByIds(staffIds.size());

        // return sqlRepository.findAll(
        //     sql,
        //     ps -> StaffParameterBinder.bindDownloadCsvByIds(ps, staffIds),
        //     StaffEntityMapper::map // ← ここで ResultSet を map
        // );
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = StaffSqlBuilder.buildDownloadCsvByIds(ids.size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> StaffParameterBinder.bindDownloadCsvByIds(ps, ids),
            StaffEntityMapper::map
        );
    }
}

