package com.kyouseipro.neo.corporation.company.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.common.simpledata.mapper.SimpleDataMapper;
import com.kyouseipro.neo.corporation.company.entity.CompanyListEntity;
import com.kyouseipro.neo.corporation.company.mapper.CompanyListEntityMapper;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

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
        String sql = "SELECT company_id, category, name, name_kana, tel_number, email FROM companies WHERE NOT (state = ?)";

        return sqlRepository.findAll(
            sql,
            (ps, v) ->  ps.setInt(1, Enums.state.DELETE.getCode()),
            CompanyListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 荷主以外全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyListEntity> findAllClient() {
        String sql = "SELECT * FROM companies WHERE NOT (state = ?) AND NOT(category = ? OR category = ?)";

        return sqlRepository.findAll(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, 0);
                ps.setInt(index++, Enums.clientCategory.PARTNER.getCode());
            },
            CompanyListEntityMapper::map
        );
    }

    /**
     * 全件取得（CategoryIDで指定）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyListEntity> findByCategoryId(int id) {
        String sql = "SELECT * FROM companies WHERE NOT (state = ?) AND category = ?";

        return sqlRepository.findAll(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
            },
            CompanyListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * コンボボックス用リスト取得（自社）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllComboOwnCompany() {
        String sql = "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND (category = ?);";

        return sqlRepository.findAll(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, 0);
            },
            SimpleDataMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllCombo() {
        String sql = "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) ORDER BY name_kana, category;";

        return sqlRepository.findAll(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            SimpleDataMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得（荷主）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllClientCombo() {
        String sql = "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND NOT (category = ? OR category = ?) ORDER BY name_kana, category;";

        return sqlRepository.findAll(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, 0);
                ps.setInt(index++, Enums.clientCategory.PARTNER.getCode());
            },
            SimpleDataMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得（発注元）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllPrimeConstractorCombo() {
        String sql = "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND category = ? ORDER BY name_kana, category;";

        return sqlRepository.findAll(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.clientCategory.SHIPPER.getCode());
            },
            SimpleDataMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得（料金表）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllPrimeConstractorComboHasOriginalPrice() {
        String sql = "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND category = ? AND is_original_price = ? ORDER BY name_kana, category;";

        return sqlRepository.findAll(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.clientCategory.SHIPPER.getCode());
                ps.setInt(index++, Enums.yesOrNo.YES.getCode());
            },
            SimpleDataMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得（CategoryIDで指定）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllComboByCategory(int category) {
        String sql = "SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND category = ?;";

        return sqlRepository.findAll(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, category);
            },
            SimpleDataMapper::map
        );
    }
}
