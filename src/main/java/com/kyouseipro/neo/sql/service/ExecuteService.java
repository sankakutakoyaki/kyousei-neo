package com.kyouseipro.neo.sql.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.QueryType;
import com.kyouseipro.neo.sql.common.SqlParamBuilder;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.SqlProvider;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExecuteService {

    private final SqlProvider sqlProvider;
    private final SqlRepository sqlRepository;
    private final SqlParamBuilder paramBuilder;

    public int execute(SelectRequest req) {

        QueryDefinition def = sqlProvider.get(req.getQueryId());

        if (def.getType() == QueryType.SELECT) {
            throw new IllegalArgumentException("更新系専用です");
        }

        List<Object> params = paramBuilder.build(def.getParamOrder(), req.getParams());

        return sqlRepository.update(def.getSql(), params);
    }
}