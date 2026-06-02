package com.kyouseipro.neo.sql.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.sql.common.QueryParamBinder;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueryExecutor {

    private final SqlRepository sqlRepository;
    private final QueryParamBinder paramBinder;

    public List<Map<String,Object>> select(QueryDefinition def, SelectRequest req) {
        List<Object> params =  paramBinder.build(def.getParamOrder(), req.getParams());
        return sqlRepository.selectMap(def.getSql(), params);
    }
}