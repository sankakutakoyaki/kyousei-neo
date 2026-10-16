package com.kyouseipro.neo.sql.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;
import com.kyouseipro.neo.sql.service.SqlTableMetadataService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogSqlProviderResolver {

    private final SqlTableMetadataService metadataService;
    private final Map<String, LogSqlProvider> genericCache = new ConcurrentHashMap<>();

    public LogSqlProvider resolve(String tableName) {
        if (!metadataService.hasLogTable(tableName)) {
            return null;
        }

        return genericCache.computeIfAbsent(
            tableName,
            name -> new GenericLogSqlProvider(name, metadataService)
        );
    }
}