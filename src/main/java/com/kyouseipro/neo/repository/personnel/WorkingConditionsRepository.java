package com.kyouseipro.neo.repository.personnel;

import java.util.List;

import org.springframework.stereotype.Repository;

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

