package com.kyouseipro.neo.domain.corporation.api.staff;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.combo.entity.ComboDto;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StaffRepository {
    private final SqlRepository sqlRepository;
    
    public List<ComboDto> findComboAll() {

        String sql = """
            SELECT * FROM staffs
            WHERE state = ?
        """;

        return sqlRepository.queryList(
            sql,
            (ps, p) -> {
                ps.setInt(1, State.INITIAL.getCode());
            },
            rs -> {
                ComboDto c = new ComboDto(
                    rs.getLong("staff_id"),
                    rs.getString("full_name"),
                    rs.getLong("office_id"));
                return c;
            }
        );
    }
}
