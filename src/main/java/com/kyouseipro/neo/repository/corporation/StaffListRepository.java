package com.kyouseipro.neo.repository.corporation;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.corporation.StaffListEntity;
import com.kyouseipro.neo.mapper.corporation.StaffListEntityMapper;
import com.kyouseipro.neo.query.parameter.corporation.StaffListParameterBinder;
import com.kyouseipro.neo.query.sql.corporation.StaffSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StaffListRepository {
    private final SqlRepository sqlRepository;

    // 全件取得
    public List<StaffListEntity> findAll() {
        String sql = StaffSqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> StaffListParameterBinder.bindFindAll(ps, null),
            StaffListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<StaffListEntity> findBySalesStaff() {
        String sql = StaffSqlBuilder.buildFindBySalesStaffSql();

        return sqlRepository.findAll(
            sql,
            ps -> StaffListParameterBinder.bindFindBySalesStaff(ps, null),
            StaffListEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
