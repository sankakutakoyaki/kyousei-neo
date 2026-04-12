package com.kyouseipro.neo.sql.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kyouseipro.neo.common.enums.system.QueryType;
import com.kyouseipro.neo.sql.common.QueryParamBinder;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.SqlProvider;
import com.kyouseipro.neo.sql.repository.BaseRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.sql.service.CsvService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QueryController {
    private final SqlProvider sqlProvider;
    private final QueryParamBinder paramBinder;
    private final SqlRepository sqlRepository;
    private final BaseRepository baseRepository;
    private final CsvService csvService;

    @PostMapping("/query")
    public Object request(@RequestBody SelectRequest req) {

        QueryDefinition def = sqlProvider.get(req.getQueryId());

        switch (def.getKind()) {

            // ========================
            // SQL（SELECT / UPDATEなど）
            // ========================
            case SQL -> {

                List<Object> params =
                    paramBinder.build(def.getParamOrder(), req.getParams());

                if (def.getType() == QueryType.SELECT) {

                    var result = sqlRepository.selectMap(def.getSql(), params);

                    return Map.of(
                        "data", result
                    );

                } else {

                    int count = sqlRepository.update(def.getSql(), params);

                    return Map.of(
                        "count", count
                    );
                }
            }

            // ========================
            // SAVE
            // ========================
            case SAVE -> {

                Map<String, Object> params = req.getParams();

                String editor = (String) params.getOrDefault("editor", "system");

                Object idValue = params.get(def.getTableMeta().idColumn());

                boolean isInsert = (idValue == null) || (Long.valueOf(idValue.toString()) == 0);

                if (isInsert) {

                    Long id = baseRepository.insert(
                        def.getTableMeta(),
                        params,
                        editor
                    );

                    return Map.of("data", id);

                } else {

                    int count = baseRepository.update(
                        def.getTableMeta(),
                        params,
                        editor
                    );

                    return Map.of("count", count);
                }
            }

            // ========================
            // INSERT
            // ========================
            case INSERT -> {

                Map<String, Object> params = req.getParams();

                String editor = (String) params.getOrDefault("editor", "system");

                Long id = baseRepository.insert(
                    def.getTableMeta(),
                    params,
                    editor
                );

                return Map.of(
                    "data", id
                );
            }

            // ========================
            // UPDATE
            // ========================
            case UPDATE -> {

                Map<String, Object> params = req.getParams();

                String editor = (String) params.getOrDefault("editor", "system");

                int count = baseRepository.update(
                    def.getTableMeta(),
                    params,
                    editor
                );

                return Map.of(
                    "count", count
                );
            }

            // ========================
            // DELETE_BY_IDS
            // ========================
            case DELETE_BY_IDS -> {

                Map<String, Object> params = req.getParams();

                List<Long> ids = (List<Long>) params.get("ids");

                String editor = (String) params.getOrDefault("editor", "system");

                int count = baseRepository.deleteByIds(
                    def.getTableMeta(),
                    ids,
                    editor
                );

                return Map.of(
                    "count", count
                );
            }

            // ========================
            // CSV_DOWNLOAD
            // ========================
            case CSV -> {
                return csvService.execute(def, req);
            }
        }

        throw new IllegalStateException("未対応のQueryKind: " + def.getKind());
    }


    // @PostMapping("/query")
    // public Object request(@RequestBody SelectRequest req) {

    //     QueryDefinition def = sqlProvider.get(req.getQueryId());

    //     if (def.getKind() == QueryKind.SQL) {

    //         List<Object> params =
    //             paramBinder.build(def.getParamOrder(), req.getParams());

    //         if (def.getType() == QueryType.SELECT) {
    //             return sqlRepository.selectMap(def.getSql(), params);
    //         } else {
    //             return sqlRepository.update(def.getSql(), params);
    //         }
    //     }

    //     if (def.getKind() == QueryKind.DELETE_BY_IDS) {

    //         List<Long> ids = (List<Long>) req.getParams().get("ids");
    //         String editor = (String) req.getParams().get("editor");

    //         return baseRepository.deleteByIds(
    //             def.getTableMeta(),
    //             ids,
    //             editor
    //         );
    //     }

    //     throw new IllegalStateException("未対応");
    // }
}
