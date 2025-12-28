package com.kyouseipro.neo.repository.corporation;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.mapper.corporation.CompanyEntityMapper;
import com.kyouseipro.neo.query.parameter.corporation.CompanyParameterBinder;
import com.kyouseipro.neo.query.sql.corporation.CompanySqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompanyRepository {
    private final SqlRepository sqlRepository;

    public Integer insert(CompanyEntity company, String editor) {
        String sql = CompanySqlBuilder.buildInsert();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> CompanyParameterBinder.bindInsert(pstmt, comp, editor),
            rs -> rs.next() ? rs.getInt("company_id") : null,
            company
        );
    }

    public Integer update(CompanyEntity company, String editor) {
        String sql = CompanySqlBuilder.buildUpdate();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> CompanyParameterBinder.bindUpdate(pstmt, comp, editor),
            rs -> rs.next() ? rs.getInt("company_id") : null,
            company
        );
    }

    public int deleteByIds(List<SimpleData> ids, String editor) {
        List<Integer> companyIds = Utilities.createSequenceByIds(ids);
        String sql = CompanySqlBuilder.buildDeleteByIds(companyIds.size());

        int result = sqlRepository.executeUpdate(
            sql,
            ps -> CompanyParameterBinder.bindDeleteByIds(ps, companyIds, editor)
        );

        return result; // 成功件数。0なら削除なし
    }

    public List<CompanyEntity> downloadCsvByIds(List<SimpleData> ids, String editor) {
        List<Integer> companyIds = Utilities.createSequenceByIds(ids);
        String sql = CompanySqlBuilder.buildDownloadCsvByIds(companyIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> CompanyParameterBinder.bindDownloadCsvByIds(ps, companyIds),
            CompanyEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // IDによる取得
    public CompanyEntity findById(int companyId) {
        String sql = CompanySqlBuilder.buildFindById();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> CompanyParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? CompanyEntityMapper.map(rs) : null,
            companyId
        );
    }

    // 全件取得
    public List<CompanyEntity> findAll() {
        String sql = CompanySqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyParameterBinder.bindFindAll(ps, null),
            CompanyEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<CompanyEntity> findAllClient() {
        String sql = CompanySqlBuilder.buildFindAllClient();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyParameterBinder.bindFindAllClient(ps, null),
            CompanyEntityMapper::map
        );
    }
}
