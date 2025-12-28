package com.kyouseipro.neo.repository.corporation;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.mapper.corporation.OfficeListEntityMapper;
import com.kyouseipro.neo.mapper.data.SimpleDataMapper;
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
        String sql = OfficeListSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            ps -> OfficeListParameterBinder.bindFindAll(ps, null),
            OfficeListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<OfficeListEntity> findAllOrderByKana() {
        String sql = OfficeListSqlBuilder.buildFindAllOrderByKana();

        return sqlRepository.findAll(
            sql,
            ps -> OfficeListParameterBinder.bindFindAll(ps, null),
            OfficeListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findAllCombo() {
        String sql = OfficeListSqlBuilder.buildFindAllCombo();

        return sqlRepository.findAll(
            sql,
            ps -> OfficeListParameterBinder.bindFindAllCombo(ps),
            SimpleDataMapper::map
        );
    }

    // 全件取得
    public List<OfficeListEntity> findByCategoryId(int categoryId) {
        String sql = OfficeListSqlBuilder.buildFindAllByCategory();

        return sqlRepository.findAll(
            sql,
            ps -> OfficeListParameterBinder.bindFindAllByCategoryId(ps, categoryId),
            OfficeListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findAllClientCombo() {
        String sql = OfficeListSqlBuilder.buildFindAllClientCombo();

        return sqlRepository.findAll(
            sql,
            ps -> OfficeListParameterBinder.bindFindAllClientCombo(ps),
            SimpleDataMapper::map
        );
    }
}
