package com.kyouseipro.neo.sql.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kyouseipro.neo.sql.common.CsvBuilder;
import com.kyouseipro.neo.sql.common.QueryParamBinder;
import com.kyouseipro.neo.sql.model.BoundSql;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CsvService {

    private final QueryParamBinder paramBinder;
    private final SqlRepository sqlRepository;

    // public ResponseEntity<byte[]> execute(QueryDefinition def, SelectRequest req) {

    //     if (def.getCsvColumns() == null || def.getCsvColumns().isEmpty()) {
    //         throw new IllegalStateException("CSVカラム定義がありません");
    //     }

    //     List<Object> params =
    //         paramBinder.build(def.getParamOrder(), req.getParams());

    //     var result = sqlRepository.selectMap(def.getSql(), params);

    //     String csv = CsvBuilder.build(result, def.getCsvColumns());

    //     byte[] bytes = ("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);

    //     return ResponseEntity.ok()
    //         .header(HttpHeaders.CONTENT_DISPOSITION,
    //                 "attachment; filename=\"data.csv\"")
    //         .body(bytes);
    // }
    public ResponseEntity<byte[]> execute(QueryDefinition def, SelectRequest req) {

        if (def.getCsvColumns() == null || def.getCsvColumns().isEmpty()) {
            throw new IllegalStateException("CSVカラム定義がありません");
        }

        BoundSql bound = paramBinder.bind(
            def.getSql(),
            def.getParamOrder(),
            req.getParams()
        );

        List<Map<String, Object>> result = sqlRepository.selectMap(
            bound.getSql(),
            bound.getParams()
        );

        String csv = CsvBuilder.build(result, def.getCsvColumns());

        byte[] bytes = ("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);

        String fileName = "download_" +
        // String fileName = def.getKind().name().toLowerCase() + "_" +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
            ".csv";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"")
            .body(bytes);
    }
}
