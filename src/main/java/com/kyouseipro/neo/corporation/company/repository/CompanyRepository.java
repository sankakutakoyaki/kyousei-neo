package com.kyouseipro.neo.corporation.company.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.corporation.company.entity.CompanyEntity;
import com.kyouseipro.neo.corporation.company.entity.CompanyEntityRequest;
import com.kyouseipro.neo.corporation.company.mapper.CompanyEntityMapper;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompanyRepository {
    private final SqlRepository sqlRepository;

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public CompanyEntity findById(int id) {
        return sqlRepository.queryOne(
            """
            SELECT * FROM companies WHERE company_id = ? AND NOT (state = ?)
            """,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, id);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            CompanyEntityMapper::map
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyEntity> findAll() {
        return sqlRepository.queryList(
            """
            SELECT * FROM companies WHERE NOT (state = ?)
            """,
            (ps, v) -> ps.setInt(1, Enums.state.DELETE.getCode()),
            CompanyEntityMapper::map
        );
    }

    /**
     * 荷主全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyEntity> findAllClient() {
        return sqlRepository.queryList(
            """
            SELECT * FROM companies WHERE NOT (category = ?) AND NOT (state = ?)
            """,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, 0);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            CompanyEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(CompanyEntityRequest entity, String editor) {
        String sql = CompanySqlBuilder.buildBulkInsert(entity);

        return sqlRepository.insert(
            sql,
            (ps, en) -> CompanyParameterBinder.bindBulkInsert(ps, en, editor),
            rs ->  rs.getInt("company_id"),
            entity
        );
    }

    /**
     * 更新。
     * @param entity
     * @return 成功件数を返す。
     */
    public int update(CompanyEntityRequest entity, String editor) {
        String sql = CompanySqlBuilder.buildBulkUpdate(entity);

        int count = sqlRepository.updateRequired(
            sql,
            (ps, e) -> CompanyParameterBinder.bindBulkUpdate(ps, e, editor),
            entity
        );

        return count;
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(IdListRequest list, String editor) {
        String sql = CompanySqlBuilder.buildDeleteByIds(list.getIds().size());

        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.updateRequired(
            sql,
            (ps, e) -> CompanyParameterBinder.bindDeleteByIds(ps, e.getIds(), editor),
            list
        );

        return count;
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<CompanyEntity> downloadCsvByIds(IdListRequest list, String editor) {
        String sql = CompanySqlBuilder.buildDownloadCsvByIds(list.getIds().size());

        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        return sqlRepository.queryList(
            sql,
            (ps, v) -> CompanyParameterBinder.bindDownloadCsvByIds(ps, list.getIds()),
            CompanyEntityMapper::map
        );
    }
}
