package com.kyouseipro.neo.domain.work.repository;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.domain.work.entity.WorkMasterEntity;
import com.kyouseipro.neo.domain.work.mapper.WorkMasterEntityMapper;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkMasterRepository {

    private final SqlRepository sqlRepository;

    public WorkMasterEntity findByWorkCode(String workCode) {

        return sqlRepository.queryOneOrNull(
            """
            SELECT *
            FROM work_masters
            WHERE state = ?
              AND work_code = ?
            """,
            (ps, v) -> {
                int index = 1;

                ps.setInt(
                    index++,
                    State.INITIAL.getCode()
                );

                ps.setString(
                    index++,
                    workCode
                );
            },
            WorkMasterEntityMapper::map
        );
    }
}