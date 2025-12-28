package com.kyouseipro.neo.repository.personnel;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.personnel.EmployeeListEntity;
import com.kyouseipro.neo.mapper.personnel.EmployeeListEntityMapper;
import com.kyouseipro.neo.query.parameter.personnel.EmployeeListParameterBinder;
import com.kyouseipro.neo.query.sql.personnel.EmployeeListSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmployeeListRepository {
    private final SqlRepository sqlRepository;

    // 全件取得
    public List<EmployeeListEntity> findAll() {
        String sql = EmployeeListSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            ps -> EmployeeListParameterBinder.bindFindAll(ps, null),
            EmployeeListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<EmployeeListEntity> findByCategoryId(int categoryId) {
        String sql = EmployeeListSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            ps -> EmployeeListParameterBinder.bindFindAllByCategoryId(ps, categoryId),
            EmployeeListEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
