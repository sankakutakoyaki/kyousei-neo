package com.kyouseipro.neo.repository.corporation;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.corporation.CompanyListEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.mapper.corporation.CompanyListEntityMapper;
import com.kyouseipro.neo.mapper.data.SimpleDataMapper;
import com.kyouseipro.neo.query.parameter.corporation.CompanyListParameterBinder;
import com.kyouseipro.neo.query.sql.corporation.CompanyListSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
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
        String sql = CompanyListSqlBuilder.buildFindAllClientSql();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAllByCategoryId(ps, categoryId),
            CompanyListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findAllComboClient() {
        String sql = CompanyListSqlBuilder.buildFindAllComboClientSql();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAllComboClient(ps, null),
            SimpleDataMapper::map
        );
    }
}
