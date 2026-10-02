package com.kyouseipro.neo.domain.corporation.api.company;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.combo.entity.ComboDto;
import com.kyouseipro.neo.common.enums.code.CompanyCategory;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompanyRepository {
    private final SqlRepository sqlRepository;

    public List<ComboDto> findComboAll() {

        String sql = """
            SELECT *
            FROM companies
            WHERE state = ?
        """;

        return sqlRepository.queryList(
            sql,
            (ps, p) -> ps.setInt(1, p),
            rs -> {
                ComboDto c = new ComboDto(
                rs.getLong("company_id"),
                rs.getString("name"));
                return c;
            },
            State.INITIAL.getCode()
        );
    }

    public List<ComboDto> findComboClientAll() {

        String sql = """
            SELECT *
            FROM companies
            WHERE state = ? AND NOT (category = ? OR category = ?)
        """;

        return sqlRepository.queryList(
            sql,
            (ps, p) -> {
                ps.setInt(1, State.INITIAL.getCode());
                ps.setInt(2, CompanyCategory.PARTNER.getCode());
                ps.setInt(3, CompanyCategory.OWN.getCode());
            },
            rs -> {
                ComboDto c = new ComboDto(
                rs.getLong("company_id"),
                rs.getString("name"));
                return c;
            }
        );
    }

    public List<ComboDto> findComboByCategory(int categoryCode) {

        String sql = """
            SELECT *
            FROM companies
            WHERE state = ? AND category = ?
        """;

        return sqlRepository.queryList(
            sql,
            (ps, p) -> {
                ps.setInt(1, State.INITIAL.getCode());
                ps.setInt(2, p);
            },
            rs -> {
                ComboDto c = new ComboDto(
                rs.getLong("company_id"),
                rs.getString("name"));
                return c;
            },
            categoryCode
        );
    }

    public Long findOwnCompanyId() {
        String sql = """
            SELECT TOP 1
                company_id
            FROM companies
            WHERE state = ?
            AND category = ?
            ORDER BY company_id
        """;
        return sqlRepository.queryOneOrNull(
            sql,
            (ps, ignored) -> {
                ps.setInt(1, State.INITIAL.getCode());
                ps.setInt(2, CompanyCategory.OWN.getCode());
            },
            rs -> rs.getLong("company_id"), null
        );
    }
}
