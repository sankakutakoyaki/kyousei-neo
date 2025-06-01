package com.kyouseipro.neo.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;
import com.kyouseipro.neo.query.parameter.WorkingConditionsParameterBinder;
import com.kyouseipro.neo.query.sql.WorkingConditionsSqlBuilder;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkingConditionsRepository {
    private final SqlRepository sqlRepository;
    private final GenericRepository genericRepository;

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
    
    // IDで検索
    public WorkingConditionsEntity findById(int workingConditionsId) {
        return genericRepository.findOne(
        "SELECT * FROM working_conditions WHERE working_conditions_id = ? AND NOT (state = ?)",
            ps -> {
                ps.setInt(1, workingConditionsId); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
            },
            WorkingConditionsEntity::new // Supplier<T>
        );
    }

    // 全件取得
    public List<WorkingConditionsEntity> findAll() {
        return genericRepository.findAll(
            "SELECT * FROM working_conditions WHERE NOT (state = ?)",
            ps -> ps.setInt(1, Enums.state.DELETE.getCode()),
            WorkingConditionsEntity::new
        );
    }
}

