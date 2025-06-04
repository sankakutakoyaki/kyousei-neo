package com.kyouseipro.neo.repository.personnel;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.personnel.EmployeeListEntity;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsListEntity;
import com.kyouseipro.neo.mapper.personnel.EmployeeListEntityMapper;
import com.kyouseipro.neo.mapper.personnel.WorkingConditionsListEntityMapper;
import com.kyouseipro.neo.query.parameter.personnel.EmployeeListParameterBinder;
import com.kyouseipro.neo.query.parameter.personnel.WorkingConditionsListParameterBinder;
import com.kyouseipro.neo.query.sql.personnel.EmployeeListSqlBuilder;
import com.kyouseipro.neo.query.sql.personnel.WorkingConditionsListSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkingConditionsListRepository {
    private final SqlRepository sqlRepository;

    // 全件取得
    public List<WorkingConditionsListEntity> findAll() {
        String sql = WorkingConditionsListSqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> WorkingConditionsListParameterBinder.bindFindAll(ps, null),
            WorkingConditionsListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<WorkingConditionsListEntity> findByCategoryId(int categoryId) {
        String sql = WorkingConditionsListSqlBuilder.buildFindAllByCategoryIdSql();

        return sqlRepository.findAll(
            sql,
            ps -> WorkingConditionsListParameterBinder.bindFindAllByCategoryId(ps, null),
            WorkingConditionsListEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
