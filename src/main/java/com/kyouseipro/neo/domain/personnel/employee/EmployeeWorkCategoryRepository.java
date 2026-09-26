package com.kyouseipro.neo.domain.personnel.employee;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.combo.entity.ComboDto;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmployeeWorkCategoryRepository {

    private final SqlRepository sqlRepository;

    public List<ComboDto> findCombo() {

        String sql = """
            SELECT
                employee_work_category_id,
                name
            FROM employee_work_categories
            WHERE state = ?
            ORDER BY
                display_order,
                employee_work_category_id
            """;

        return sqlRepository.queryList(
            sql,
            (ps, p) -> {
                ps.setInt(1, State.INITIAL.getCode());
            },
            rs -> new ComboDto(
                rs.getLong("employee_work_category_id"),
                rs.getString("name")
            )
        );
    }
}