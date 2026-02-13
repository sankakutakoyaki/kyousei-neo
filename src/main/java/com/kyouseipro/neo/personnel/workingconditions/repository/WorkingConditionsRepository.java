package com.kyouseipro.neo.personnel.workingconditions.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
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
    public WorkingConditionsEntity findById(int id) {
        String sql = WorkingConditionsSqlBuilder.buildFindById();

        return sqlRepository.queryOne(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
            },
            WorkingConditionsEntityMapper::map
        );
    }

    /**
     * IDによる取得（EmployeeIDで指定）。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public WorkingConditionsEntity findByEmployeeId(int id) {
        String sql = WorkingConditionsSqlBuilder.buildFindByEmployeeId();

        return sqlRepository.queryOne(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
            },
            WorkingConditionsEntityMapper::map
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<WorkingConditionsEntity> findAll() {
        String sql = WorkingConditionsSqlBuilder.buildFindAll();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> ps.setInt(1, Enums.state.DELETE.getCode()),
            WorkingConditionsEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(WorkingConditionsEntity entity, String editor) {
        String sql = WorkingConditionsSqlBuilder.buildInsert();

        return sqlRepository.insert(
            sql,
            (ps, en) -> WorkingConditionsParameterBinder.bindInsert(ps, en, editor),
            rs -> rs.getInt("working_conditions_id"),
            entity
        );
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public int update(WorkingConditionsEntity entity, String editor) {
        String sql = WorkingConditionsSqlBuilder.buildUpdate();

        int count = sqlRepository.updateRequired(
            sql,
            (ps, e) -> WorkingConditionsParameterBinder.bindUpdate(ps, e, editor),
            entity
        );

        return count;
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
        
        String sql = WorkingConditionsSqlBuilder.buildDeleteByIds(list.getIds().size());

        int count = sqlRepository.updateRequired(
            sql,
            (ps, e) -> WorkingConditionsParameterBinder.bindDeleteByIds(ps, e.getIds(), editor),
            list
        );

        return count;
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<WorkingConditionsEntity> downloadCsvByIds(IdListRequest list, String editor) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        String sql = WorkingConditionsSqlBuilder.buildDownloadCsvByIds(list.getIds().size());

        return sqlRepository.queryList(
            sql,
            (ps, e) -> WorkingConditionsParameterBinder.bindDownloadCsvByIds(ps, e.getIds()),
            WorkingConditionsEntityMapper::map,
            list
        );
    }
}

