package com.kyouseipro.neo.sql.provider;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueryHandlerProvider {
    private final List<QueryHandler> handlers;

    public QueryHandler get(QueryKind kind) {
        return handlers.stream()
                .filter(h -> h.supports(kind))
                .findFirst()
                .orElseThrow(() ->  new IllegalArgumentException("Handler not found : " + kind));
    }
}