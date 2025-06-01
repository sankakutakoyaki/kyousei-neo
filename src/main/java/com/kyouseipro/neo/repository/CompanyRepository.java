package com.kyouseipro.neo.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.query.parameter.CompanyParameterBinder;
import com.kyouseipro.neo.query.sql.CompanySqlBuilder;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompanyRepository {
    private final SqlRepository sqlRepository;
    private final GenericRepository genericRepository;

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

    // IDによる取得
    public CompanyEntity findById(int companyId) {
        String sql = CompanySqlBuilder.buildFindByIdSql();

        return genericRepository.findOne(
            sql,
            pstmt -> {
                pstmt.setInt(1, companyId); // キャプチャした変数を使う
                pstmt.setInt(2, Enums.state.DELETE.getCode());
            },
            CompanyEntity::fromResultSet,
            companyId
        );
    }
        // return genericRepository.findOne(
        //     sql,
        //     ps -> {
        //         ps.setInt(1, companyId); // 1番目の ?
        //         ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
        //     },
        //     CompanyEntity::new // Supplier<T>
        // );
    // }


    // 全件取得
    public List<CompanyEntity> findAll() {
        return genericRepository.findAll(
            "SELECT * FROM companies WHERE NOT (state = ?)",
            ps -> ps.setInt(1, Enums.state.DELETE.getCode()),
            CompanyEntity::new
        );
    }

    // 全件取得
    public List<CompanyEntity> findAllClient() {
        return genericRepository.findAll(
            "SELECT * FROM companies WHERE NOT (category = ?) AND NOT (state = ?)",
            ps -> {
                ps.setInt(1, 0); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
            },
            CompanyEntity::new
        );
    }
}
