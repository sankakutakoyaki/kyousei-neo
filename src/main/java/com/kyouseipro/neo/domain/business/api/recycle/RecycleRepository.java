package com.kyouseipro.neo.domain.business.api.recycle;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.combo.entity.ComboDto;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecycleRepository {
    private final SqlRepository sqlRepository;

    public List<ComboDto> findItemCombo() {

        String sql = """
            SELECT *
            FROM recycle_items
            WHERE state = ? ORDER BY code
        """;

        return sqlRepository.queryList(
            sql,
            (ps, p) -> {
                ps.setInt(1, State.INITIAL.getCode());
            },
            rs -> {
                ComboDto c = new ComboDto(
                    rs.getLong("code"),
                    rs.getString("name"),
                    rs.getLong("recycle_item_id"));
                return c;
            }
        );
    }
}
