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

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<EmployeeListEntity> findAll() {
        String sql = EmployeeListSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> EmployeeListParameterBinder.bindFindAll(ps, null),
            EmployeeListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 全件取得（CategoryIDで指定）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<EmployeeListEntity> findByCategoryId(int id) {
        String sql = EmployeeListSqlBuilder.buildFindAllByCategoryId();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> EmployeeListParameterBinder.bindFindAllByCategoryId(ps, id),
            EmployeeListEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
