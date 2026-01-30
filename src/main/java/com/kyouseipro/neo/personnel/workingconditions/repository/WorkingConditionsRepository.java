package com.kyouseipro.neo.personnel.workingconditions.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.personnel.workingconditions.entity.WorkingConditionsEntity;
import com.kyouseipro.neo.personnel.workingconditions.mapper.WorkingConditionsEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkingConditionsRepository {
    private final SqlRepository sqlRepository;

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public Optional<WorkingConditionsEntity> findById(int id) {
        String sql = WorkingConditionsSqlBuilder.buildFindById();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> WorkingConditionsParameterBinder.bindFindById(ps, en),
            rs -> rs.next() ? WorkingConditionsEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * IDによる取得（EmployeeIDで指定）。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public Optional<WorkingConditionsEntity> findByEmployeeId(int id) {
        String sql = WorkingConditionsSqlBuilder.buildFindByEmployeeId();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> WorkingConditionsParameterBinder.bindFindByEmployeeId(ps, en),
            rs -> rs.next() ? WorkingConditionsEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<WorkingConditionsEntity> findAll() {
        String sql = WorkingConditionsSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> WorkingConditionsParameterBinder.bindFindAll(ps, null),
            WorkingConditionsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(WorkingConditionsEntity entity, String editor) {
        String sql = WorkingConditionsSqlBuilder.buildInsert();

        // // return sqlRepository.execute(
        // //     sql,
        // //     (pstmt, entity) -> WorkingConditionsParameterBinder.bindInsert(pstmt, entity, editor),
        // //     rs -> rs.next() ? rs.getInt("working_conditions_id") : null,
        // //     w
        // // );
        // try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> WorkingConditionsParameterBinder.bindInsert(ps, en, editor),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("working_conditions_id");

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
    public int update(WorkingConditionsEntity entity, String editor) {
        String sql = WorkingConditionsSqlBuilder.buildUpdate();

        // // return sqlRepository.execute(
        // //     sql,
        // //     (pstmt, entity) -> WorkingConditionsParameterBinder.bindUpdate(pstmt, entity, editor),
        // //     rs -> rs.next() ? rs.getInt("working_conditions_id") : null,
        // //     w
        // // );
        // try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> WorkingConditionsParameterBinder.bindUpdate(ps, entity, editor)
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
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(IdListRequest list, String editor) {
        // List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = WorkingConditionsSqlBuilder.buildDeleteByIds(list.getIds().size());

        // int result = sqlRepository.executeUpdate(
        //     sql,
        //     ps -> WorkingConditionsParameterBinder.bindDeleteByIds(ps, workingConditionsIds, editor)
        // );

        // return result; // 成功件数。0なら削除なし
            if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> WorkingConditionsParameterBinder.bindDeleteByIds(ps, list.getIds(), editor)
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
    public List<WorkingConditionsEntity> downloadCsvByIds(IdListRequest list, String editor) {
        // List<Integer> workingConditionsIds = Utilities.createSequenceByIds(ids);
        // String sql = WorkingConditionsSqlBuilder.buildDownloadCsvByIds(workingConditionsIds.size());

        // return sqlRepository.findAll(
        //     sql,
        //     ps -> WorkingConditionsParameterBinder.bindDownloadCsvByIds(ps, workingConditionsIds),
        //     WorkingConditionsEntityMapper::map // ← ここで ResultSet を map
        // );
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        // List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = WorkingConditionsSqlBuilder.buildDownloadCsvByIds(list.getIds().size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> WorkingConditionsParameterBinder.bindDownloadCsvByIds(ps, list.getIds()),
            WorkingConditionsEntityMapper::map
        );
    }
}

