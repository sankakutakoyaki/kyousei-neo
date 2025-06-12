package com.kyouseipro.neo.repository.personnel;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;
import com.kyouseipro.neo.mapper.personnel.WorkingConditionsEntityMapper;
import com.kyouseipro.neo.query.parameter.personnel.WorkingConditionsParameterBinder;
import com.kyouseipro.neo.query.sql.personnel.WorkingConditionsSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkingConditionsRepository {
    private final SqlRepository sqlRepository;

    // INSERT
    public Integer insertWorkingConditions(WorkingConditionsEntity w, String editor) {
        String sql = WorkingConditionsSqlBuilder.buildInsertSql();

        return sqlRepository.execute(
            sql,
            (pstmt, entity) -> WorkingConditionsParameterBinder.bindInsertParameters(pstmt, entity, editor),
            rs -> rs.next() ? rs.getInt("working_conditions_id") : null,
            w
        );
    }

    // UPDATE
    public Integer updateWorkingConditions(WorkingConditionsEntity w, String editor) {
        String sql = WorkingConditionsSqlBuilder.buildUpdateSql();

        return sqlRepository.execute(
            sql,
            (pstmt, entity) -> WorkingConditionsParameterBinder.bindUpdateParameters(pstmt, entity, editor),
            rs -> rs.next() ? rs.getInt("working_conditions_id") : null,
            w
        );
    }

    // DELETE
    public int deleteWorkingConditionsByIds(List<SimpleData> ids, String editor) {
        List<Integer> workingConditionsIds = Utilities.createSequenceByIds(ids);
        String sql = WorkingConditionsSqlBuilder.buildDeleteWorkingConditionsForIdsSql(workingConditionsIds.size());

        int result = sqlRepository.executeUpdate(
            sql,
            ps -> WorkingConditionsParameterBinder.bindDeleteForIds(ps, workingConditionsIds, editor)
        );

        return result; // 成功件数。0なら削除なし
    }

    // CSV
    public List<WorkingConditionsEntity> downloadCsvWorkingConditionsByIds(List<SimpleData> ids, String editor) {
        List<Integer> workingConditionsIds = Utilities.createSequenceByIds(ids);
        String sql = WorkingConditionsSqlBuilder.buildDownloadCsvWorkingConditionsForIdsSql(workingConditionsIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> WorkingConditionsParameterBinder.bindDownloadCsvForIds(ps, workingConditionsIds),
            WorkingConditionsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // IDによる取得
    public WorkingConditionsEntity findById(int workingConditionsId) {
        String sql = WorkingConditionsSqlBuilder.buildFindByIdSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> WorkingConditionsParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? WorkingConditionsEntityMapper.map(rs) : null,
            workingConditionsId
        );
    }

    // IDによる取得
    public WorkingConditionsEntity findByEmployeeId(int employeeId) {
        String sql = WorkingConditionsSqlBuilder.buildFindByEmployeeIdSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> WorkingConditionsParameterBinder.bindFindByEmployeeId(pstmt, comp),
            rs -> rs.next() ? WorkingConditionsEntityMapper.map(rs) : null,
            employeeId
        );
    }

    // 全件取得
    public List<WorkingConditionsEntity> findAll() {
        String sql = WorkingConditionsSqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> WorkingConditionsParameterBinder.bindFindAll(ps, null),
            WorkingConditionsEntityMapper::map // ← ここで ResultSet を map
        );
    }
}

