// package com.kyouseipro.neo.sql.service;

// import java.util.List;
// import java.util.Map;

// import org.springframework.stereotype.Service;

// import com.kyouseipro.neo.common.enums.system.QueryType;
// import com.kyouseipro.neo.sql.common.QueryParamBinder;
// import com.kyouseipro.neo.sql.model.QueryDefinition;
// import com.kyouseipro.neo.sql.model.SelectRequest;
// import com.kyouseipro.neo.sql.provider.SqlProvider;
// import com.kyouseipro.neo.sql.repository.SqlRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class SelectService {

//     private final SqlProvider sqlProvider;
//     private final SqlRepository sqlRepository;
//     private final QueryParamBinder paramBuilder;

//     public List<Map<String, Object>> select(SelectRequest req) {

//         QueryDefinition def = sqlProvider.get(req.getQueryId());

//         if (def.getType() != QueryType.SELECT) {
//             throw new IllegalArgumentException("SELECT専用です");
//         }

//         List<Object> params = paramBuilder.build(def.getParamOrder(), req.getParams());

//         return sqlRepository.selectMap(def.getSql(), params);
//     }
// }