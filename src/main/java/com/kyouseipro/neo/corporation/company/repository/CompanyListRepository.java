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
        return sqlRepository.queryList(
            """
            SELECT company_id, category, name, name_kana, tel_number, email FROM companies WHERE NOT (state = ?) ORDER BY name_kana
            """,
            (ps, v) ->  ps.setInt(1, Enums.state.DELETE.getCode()),
            CompanyListEntityMapper::map
        );
    }

    /**
     * パートナー以外の会社を全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyListEntity> findAllClient() {
        return sqlRepository.queryList(
            """
            SELECT * FROM companies WHERE NOT (state = ?) AND NOT (category = ? OR category = ?) ORDER BY category, name_kana;
            """,
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
     * 荷主以外全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyListEntity> findAllPartner() {
        return sqlRepository.queryList(
            """
            SELECT * FROM companies WHERE NOT (state = ?) AND category = ? ORDER BY name_kana
            """,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
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
        return sqlRepository.queryList(
            """
            SELECT * FROM companies WHERE NOT (state = ?) AND category = ? ORDER BY name_kana
            """,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, id);
            },
            CompanyListEntityMapper::map
        );
    }

    /**
     * コンボボックス用リスト取得（自社）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllComboOwnCompany() {
        return sqlRepository.queryList(
            """
            SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND (category = ?);
            """,
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
        return sqlRepository.queryList(
            """
            SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) ORDER BY category, name_kana;
            """,
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
        return sqlRepository.queryList(
            """
            SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND NOT (category = ? OR category = ?) ORDER BY name_kana;
            """,
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
     * コンボボックス用リスト取得（荷主）。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<SimpleData> findAllPartnerCombo() {
        return sqlRepository.queryList(
            """
            SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND category = ? ORDER BY name_kana, category;
            """,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
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
        return sqlRepository.queryList(
            """
            SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND category = ? ORDER BY name_kana, category;
            """,
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
        return sqlRepository.queryList(
            """
            SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND category = ? AND is_original_price = ? ORDER BY name_kana, category;
            """,
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
        return sqlRepository.queryList(
            """
            SELECT company_id as number, name as text FROM companies WHERE NOT (state = ?) AND category = ? ORDER BY name_kana;
            """,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, category);
            },
            SimpleDataMapper::map
        );
    }
}
