package com.kyouseipro.neo.personnel.workingconditions.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.personnel.workingconditions.entity.WorkingConditionsListEntity;
import com.kyouseipro.neo.personnel.workingconditions.mapper.WorkingConditionsListEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkingConditionsListRepository {
    private final SqlRepository sqlRepository;

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<WorkingConditionsListEntity> findAll() {
        String sql = WorkingConditionsListSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> WorkingConditionsListParameterBinder.bindFindAll(ps, null),
            WorkingConditionsListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 全件取得（CategoryIDで指定）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<WorkingConditionsListEntity> findByCategoryId(int id) {
        String sql = WorkingConditionsListSqlBuilder.buildFindAllByCategoryId();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> WorkingConditionsListParameterBinder.bindFindAllByCategoryId(ps, id),
            WorkingConditionsListEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
