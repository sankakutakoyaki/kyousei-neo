package com.kyouseipro.neo.repository.corporation;

import java.util.List;

import com.kyouseipro.neo.entity.corporation.CompanyListEntity;
import com.kyouseipro.neo.mapper.corporation.CompanyListEntityMapper;
import com.kyouseipro.neo.query.parameter.company.CompanyListParameterBinder;
import com.kyouseipro.neo.query.sql.company.CompanyListSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompanyListRepository {
    private final SqlRepository sqlRepository;

    // 全件取得
    public List<CompanyListEntity> findAll() {
        String sql = CompanyListSqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAll(ps, null),
            CompanyListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<CompanyListEntity> findAllClient() {
        String sql = CompanyListSqlBuilder.buildFindAllClientSql();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAllClient(ps, null),
            CompanyListEntityMapper::map
        );
    }

    // 全件取得
    public List<CompanyListEntity> findByCategoryId(int categoryId) {
        String sql = CompanyListSqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAllByCategoryId(ps, null),
            CompanyListEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
