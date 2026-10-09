package com.kyouseipro.neo.domain.personnel.position;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmployeePositionRepository {

    private final SqlRepository sqlRepository;

    public List<Map<String, Object>> findCombo() {
        return sqlRepository.selectMap(
            """
            SELECT
                employee_position_id AS value,
                position_name AS label
            FROM employee_positions
            WHERE state = ?
            ORDER BY
                display_order,
                employee_position_id
            """,
            List.of(
                State.INITIAL.getCode()
            )
        );
}
}