package com.kyouseipro.neo.corporation.company.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.exception.BusinessException;
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
    public Optional<CompanyEntity> findById(int id) {
        String sql = "SELECT * FROM companies WHERE company_id = ? AND NOT (state = ?)";

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> {
                int index = 1;
                ps.setInt(index++, id);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            rs -> rs.next() ? CompanyEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyEntity> findAll() {
        String sql = "SELECT * FROM companies WHERE NOT (state = ?)";

        return sqlRepository.findAll(
            sql,
            (ps, v) -> ps.setInt(1, Enums.state.DELETE.getCode()),
            CompanyEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 荷主全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyEntity> findAllClient() {
        String sql = "SELECT * FROM companies WHERE NOT (category = ?) AND NOT (state = ?)";

        return sqlRepository.findAll(
            sql,
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

        return sqlRepository.executeRequired(
            sql,
            (ps, en) -> CompanyParameterBinder.bindBulkInsert(ps, en, editor),
            rs -> {
                if (!rs.next()) {
                    throw new BusinessException("登録に失敗しました");
                }
                int id = rs.getInt("company_id");

                if (rs.next()) {
                    throw new IllegalStateException("ID取得結果が複数行です");
                }
                return id;
            },
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

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> CompanyParameterBinder.bindBulkUpdate(ps, entity, editor)
        );

        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

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

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> CompanyParameterBinder.bindDeleteByIds(ps, list.getIds(), editor)
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

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

        return sqlRepository.findAll(
            sql,
            (ps, v) -> CompanyParameterBinder.bindDownloadCsvByIds(ps, list.getIds()),
            CompanyEntityMapper::map
        );
    }
}
