package com.kyouseipro.neo.sql.provider;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.LogSqlProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogSqlProviderResolver {

    private final Map<String, LogSqlProvider> providers;

    public LogSqlProvider resolve(String tableName) {

        // 完全一致
        if (providers.containsKey(tableName)) {
            return providers.get(tableName);
        }

        // fallback
        return providers.get("default");
    }
}