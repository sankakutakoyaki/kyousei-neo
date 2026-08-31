package com.kyouseipro.neo.domain.master.item.repository;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.domain.master.item.model.ItemMasterEntity;
import com.kyouseipro.neo.domain.master.item.mapper.ItemMasterEntityMapper;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ItemMasterRepository {

    private final SqlRepository sqlRepository;

    public ItemMasterEntity findByJanCode(String janCode) {

        return sqlRepository.queryOneOrNull(
            """
            SELECT *
            FROM item_masters
            WHERE state = ?
              AND jan_code = ?
            """,
            (ps, v) -> {
                int index = 1;

                ps.setInt(index++, State.INITIAL.getCode());
                ps.setString(index++, janCode);
            },
            ItemMasterEntityMapper::map
        );
    }
}