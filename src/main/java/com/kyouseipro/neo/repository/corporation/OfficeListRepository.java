package com.kyouseipro.neo.repository.corporation;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.mapper.corporation.OfficeListEntityMapper;
import com.kyouseipro.neo.query.parameter.corporation.OfficeListParameterBinder;
import com.kyouseipro.neo.query.sql.corporation.OfficeListSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OfficeListRepository {
    private final SqlRepository sqlRepository;

    // 全件取得
    public List<OfficeListEntity> findAll() {
        String sql = OfficeListSqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> OfficeListParameterBinder.bindFindAll(ps, null),
            OfficeListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<OfficeListEntity> findAllClient() {
        String sql = OfficeListSqlBuilder.buildFindAllClientSql();

        return sqlRepository.findAll(
            sql,
            ps -> OfficeListParameterBinder.bindFindAllClient(ps, null),
            OfficeListEntityMapper::map
        );
    }

    // 全件取得
    public List<OfficeListEntity> findByCategoryId(int categoryId) {
        String sql = OfficeListSqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> OfficeListParameterBinder.bindFindAllByCategoryId(ps, null),
            OfficeListEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
