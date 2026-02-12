package com.kyouseipro.neo.corporation.office.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.common.simpledata.mapper.SimpleDataMapper;
import com.kyouseipro.neo.corporation.office.entity.OfficeListEntity;
import com.kyouseipro.neo.corporation.office.mapper.OfficeListEntityMapper;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OfficeListRepository {
    private final SqlRepository sqlRepository;

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<OfficeListEntity> findAll() {
        String sql = OfficeListSqlBuilder.buildFindAll();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            OfficeListEntityMapper::map
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す(カナ昇順)
     */
    public List<OfficeListEntity> findAllOrderByKana() {
        String sql = OfficeListSqlBuilder.buildFindAllOrderByKana();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            OfficeListEntityMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllCombo() {
        return sqlRepository.queryList(
            """
            SELECT office_id as number, name as text FROM offices WHERE NOT (state = ?) ORDER BY name_kana;
            """,
            (ps, v) -> ps.setInt(1, Enums.state.DELETE.getCode()),
            SimpleDataMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findByOwnCombo() {
        return sqlRepository.queryList(
            """
            SELECT office_id as number, name as text FROM offices WHERE NOT (state = ?) AND company_id = ?
            """,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Utilities.getOwnCompanyId());
            },
            SimpleDataMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得（CategoryIDで指定）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<OfficeListEntity> findByCategoryId(int categoryId) {
        String sql = OfficeListSqlBuilder.buildFindAllByCategory();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, categoryId);
            },
            OfficeListEntityMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得（荷主）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllClientCombo() {
        String sql = OfficeListSqlBuilder.buildFindAllClientCombo();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.clientCategory.SHIPPER.getCode());
            },
            SimpleDataMapper::map
        );
    }
}
