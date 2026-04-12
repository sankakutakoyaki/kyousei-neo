package com.kyouseipro.neo.domain.corporation.company;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.enums.code.ClientCategory;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.master.combo.ComboDto;
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
            WHERE state = ? AND NOT (category = ? AND category = ?)
        """;

        return sqlRepository.queryList(
            sql,
            (ps, p) -> {
                ps.setInt(1, State.INITIAL.getCode());
                ps.setInt(2, ClientCategory.PARTNER.getCode());
                ps.setInt(2, 0);
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
}
