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
        String sql = CompanyListSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAll(ps, null),
            CompanyListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<CompanyListEntity> findAllClient() {
        String sql = CompanyListSqlBuilder.buildFindAllClient();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAllClient(ps, null),
            CompanyListEntityMapper::map
        );
    }

    // 全件取得
    public List<CompanyListEntity> findByCategoryId(int categoryId) {
        String sql = CompanyListSqlBuilder.buildFindAllClient();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAllByCategoryId(ps, categoryId),
            CompanyListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findAllComboOwnCompany() {
        String sql = CompanyListSqlBuilder.buildFindAllComboOwnCompany();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAllComboOwnCompany(ps, null),
            SimpleDataMapper::map
        );
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findAllCombo() {
        String sql = CompanyListSqlBuilder.buildFindAllCombo();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAllCombo(ps, null),
            SimpleDataMapper::map
        );
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findAllClientCombo() {
        String sql = CompanyListSqlBuilder.buildFindAllClientCombo();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAllClientCombo(ps, null),
            SimpleDataMapper::map
        );
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findAllPrimeConstractorCombo() {
        String sql = CompanyListSqlBuilder.buildFindAllPrimeConstractorCombo();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAllPrimeConstractorCombo(ps, null),
            SimpleDataMapper::map
        );
    }

    // 料金表コンボボックス用リスト取得
    public List<SimpleData> findAllPrimeConstractorComboHasOriginalPrice() {
        String sql = CompanyListSqlBuilder.buildFindAllPrimeConstractorComboHasOriginalPrice();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAllPrimeConstractorComboHasOriginalPrice(ps, null),
            SimpleDataMapper::map
        );
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findAllComboByCategory(int category) {
        String sql = CompanyListSqlBuilder.buildFindAllComboByCategory();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyListParameterBinder.bindFindAllComboByCategory(ps, category),
            SimpleDataMapper::map
        );
    }
}
