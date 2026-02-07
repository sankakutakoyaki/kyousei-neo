package com.kyouseipro.neo.corporation.staff.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.corporation.staff.entity.StaffEntity;
import com.kyouseipro.neo.corporation.staff.entity.StaffEntityRequest;
import com.kyouseipro.neo.corporation.staff.mapper.StaffEntityMapper;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

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
    public int insert(StaffEntityRequest entity, String editor) {
        String sql = StaffSqlBuilder.buildBulkInsert(entity);

        // // return sqlRepository.executeRequired(
        // //     sql,
        // //     (ps, en) -> StaffParameterBinder.bindInsert(pstmt, en, editor),
        // //     rs -> rs.next() ? rs.getInt("staff_id") : null,
        // //     staff
        // // );
        // try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> StaffParameterBinder.bindBulkInsert(ps, en, editor),
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
        // } catch (RuntimeException e) {
        //     if (SqlExceptionUtil.isDuplicateKey(e)) {
        //         throw new BusinessException("このコードはすでに使用されています。");
        //     }
        //     throw e;
        // }
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public int update(StaffEntityRequest entity, String editor) {
        String sql = StaffSqlBuilder.buildBulkUpdate(entity);

        // // return sqlRepository.execute(
        // //     sql,
        // //     (pstmt, entity) -> StaffParameterBinder.bindUpdate(pstmt, entity, editor),
        // //     rs -> rs.next() ? rs.getInt("staff_id") : null,
        // //     staff
        // // );
        // try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> StaffParameterBinder.bindBulkUpdate(ps, entity, editor)
            );

            if (count == 0) {
                throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
            }

            return count;

        // } catch (RuntimeException e) {
        //     if (SqlExceptionUtil.isDuplicateKey(e)) {
        //         throw new BusinessException("このコードはすでに使用されています。");
        //     }
        //     throw e;
        // }
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(IdListRequest list, String editor) {
        // List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = StaffSqlBuilder.buildDeleteByIds(list.getIds().size());

        // int result = sqlRepository.executeUpdate(
        //     sql,
        //     ps -> StaffParameterBinder.bindDeleteByIds(ps, staffIds, editor)
        // );

        // return result; // 成功件数。0なら削除なし
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> StaffParameterBinder.bindDeleteByIds(ps, list.getIds(), editor)
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<StaffEntity> downloadCsvByIds(IdListRequest list, String editor) {
        // List<Integer> staffIds = Utilities.createSequenceByIds(ids);
        // String sql = StaffSqlBuilder.buildDownloadCsvByIds(staffIds.size());

        // return sqlRepository.findAll(
        //     sql,
        //     ps -> StaffParameterBinder.bindDownloadCsvByIds(ps, staffIds),
        //     StaffEntityMapper::map // ← ここで ResultSet を map
        // );
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        // List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = StaffSqlBuilder.buildDownloadCsvByIds(list.getIds().size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> StaffParameterBinder.bindDownloadCsvByIds(ps, list.getIds()),
            StaffEntityMapper::map
        );
    }
}

