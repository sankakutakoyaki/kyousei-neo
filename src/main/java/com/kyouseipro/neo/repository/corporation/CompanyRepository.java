package com.kyouseipro.neo.repository.corporation;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.mapper.corporation.CompanyEntityMapper;
import com.kyouseipro.neo.query.parameter.company.CompanyParameterBinder;
import com.kyouseipro.neo.query.sql.company.CompanySqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompanyRepository {
    private final SqlRepository sqlRepository;

    public Integer insertCompany(CompanyEntity company, String editor) {
        String sql = CompanySqlBuilder.buildInsertCompanySql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> CompanyParameterBinder.bindInsertCompanyParameters(pstmt, comp, editor),
            rs -> rs.next() ? rs.getInt("company_id") : null,
            company
        );
    }

    public Integer updateCompany(CompanyEntity company, String editor) {
        String sql = CompanySqlBuilder.buildUpdateCompanySql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> CompanyParameterBinder.bindUpdateCompanyParameters(pstmt, comp, editor),
            rs -> rs.next() ? rs.getInt("company_id") : null,
            company
        );
    }

    public int deleteCompanyByIds(List<SimpleData> ids, String editor) {
        List<Integer> companyIds = Utilities.createSequenceByIds(ids);
        String sql = CompanySqlBuilder.buildDeleteCompanyForIdsSql(companyIds.size());

        int result = sqlRepository.executeUpdate(
            sql,
            ps -> CompanyParameterBinder.bindDeleteForIds(ps, companyIds)
        );

        return result; // 成功件数。0なら削除なし
    }

    public List<CompanyEntity> downloadCsvCompanyByIds(List<SimpleData> ids, String editor) {
        List<Integer> companyIds = Utilities.createSequenceByIds(ids);
        String sql = CompanySqlBuilder.buildDownloadCsvCompanyForIdsSql(companyIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> CompanyParameterBinder.bindFindAll(ps, null),
            CompanyEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // IDによる取得
    public CompanyEntity findById(int companyId) {
        String sql = CompanySqlBuilder.buildFindByIdSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> CompanyParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? CompanyEntityMapper.map(rs) : null,
            companyId
        );
    }

    // 全件取得
    public List<CompanyEntity> findAll() {
        String sql = CompanySqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyParameterBinder.bindFindAll(ps, null),
            CompanyEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<CompanyEntity> findAllClient() {
        String sql = CompanySqlBuilder.buildFindAllClientSql();

        return sqlRepository.findAll(
            sql,
            ps -> CompanyParameterBinder.bindFindAllClient(ps, null),
            CompanyEntityMapper::map
        );
    }
}
