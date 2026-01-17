package com.kyouseipro.neo.repository.corporation;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.corporation.CompanyListEntity;
import com.kyouseipro.neo.entity.dto.SimpleData;
import com.kyouseipro.neo.mapper.corporation.CompanyListEntityMapper;
import com.kyouseipro.neo.mapper.dto.SimpleDataMapper;
import com.kyouseipro.neo.query.parameter.corporation.CompanyListParameterBinder;
import com.kyouseipro.neo.query.sql.corporation.CompanyListSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompanyListRepository {
    private final SqlRepository sqlRepository;

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyListEntity> findAll() {
        String sql = CompanyListSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> CompanyListParameterBinder.bindFindAll(ps, null),
            CompanyListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 荷主以外全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyListEntity> findAllClient() {
        String sql = CompanyListSqlBuilder.buildFindAllClient();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> CompanyListParameterBinder.bindFindAllClient(ps, null),
            CompanyListEntityMapper::map
        );
    }

    /**
     * 全件取得（CategoryIDで指定）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyListEntity> findByCategoryId(int id) {
        String sql = CompanyListSqlBuilder.buildFindAllByCategoryId();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> CompanyListParameterBinder.bindFindAllByCategoryId(ps, id),
            CompanyListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * コンボボックス用リスト取得（自社）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllComboOwnCompany() {
        String sql = CompanyListSqlBuilder.buildFindAllComboOwnCompany();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> CompanyListParameterBinder.bindFindAllComboOwnCompany(ps, null),
            SimpleDataMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllCombo() {
        String sql = CompanyListSqlBuilder.buildFindAllCombo();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> CompanyListParameterBinder.bindFindAllCombo(ps, null),
            SimpleDataMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得（荷主）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllClientCombo() {
        String sql = CompanyListSqlBuilder.buildFindAllClientCombo();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> CompanyListParameterBinder.bindFindAllClientCombo(ps, null),
            SimpleDataMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得（発注元）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllPrimeConstractorCombo() {
        String sql = CompanyListSqlBuilder.buildFindAllPrimeConstractorCombo();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> CompanyListParameterBinder.bindFindAllPrimeConstractorCombo(ps, null),
            SimpleDataMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得（料金表）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllPrimeConstractorComboHasOriginalPrice() {
        String sql = CompanyListSqlBuilder.buildFindAllPrimeConstractorComboHasOriginalPrice();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> CompanyListParameterBinder.bindFindAllPrimeConstractorComboHasOriginalPrice(ps, null),
            SimpleDataMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得（CategoryIDで指定）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllComboByCategory(int category) {
        String sql = CompanyListSqlBuilder.buildFindAllComboByCategory();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> CompanyListParameterBinder.bindFindAllComboByCategory(ps, category),
            SimpleDataMapper::map
        );
    }
}
