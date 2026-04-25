package com.kyouseipro.neo.domain.corporation.office;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.enums.code.CompanyCategory;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.master.combo.ComboDto;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OfficeRepository {
    private final SqlRepository sqlRepository;
    
    public List<ComboDto> findComboClientAll() {

        String sql = """
            SELECT o.* FROM offices o
            INNER JOIN companies c ON c.company_id = o.company_id AND c.state = ? 
            WHERE o.state = ? AND NOT (c.category = ? OR c.category = ?)
        """;

        return sqlRepository.queryList(
            sql,
            (ps, p) -> {
                ps.setInt(1, State.INITIAL.getCode());
                ps.setInt(2, State.INITIAL.getCode());
                ps.setInt(3,CompanyCategory.PARTNER.getCode());
                ps.setInt(4, 0);
            },
            rs -> {
                ComboDto c = new ComboDto(
                    rs.getLong("office_id"),
                    rs.getString("name"),
                    rs.getLong("company_id"));
                return c;
            }
        );
    }

    public List<ComboDto> findComboByCategory(int categoryCode) {

        String sql = """
            SELECT *
            FROM offices
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
                rs.getLong("office_id"),
                rs.getString("name"));
                return c;
            },
            categoryCode
        );
    }
}
