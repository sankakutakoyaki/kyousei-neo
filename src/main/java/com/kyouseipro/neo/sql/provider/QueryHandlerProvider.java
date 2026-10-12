package com.kyouseipro.neo.sql.provider;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;

@Component
public class QueryHandlerProvider {
    private final Map<QueryKind, QueryHandler> handlers;

    public QueryHandlerProvider(List<QueryHandler> candidates) {
        Map<QueryKind, QueryHandler> registered = new EnumMap<>(QueryKind.class);
        for (QueryHandler handler : candidates) {
            for (QueryKind kind : QueryKind.values()) {
                if (!handler.supports(kind)) continue;
                QueryHandler previous = registered.putIfAbsent(kind, handler);
                if (previous != null) {
                    throw new IllegalStateException("QueryHandlerの重複登録: " + kind
                        + " / " + ClassUtils.getUserClass(previous).getName()
                        + " / " + ClassUtils.getUserClass(handler).getName());
                }
            }
        }
        handlers = Map.copyOf(registered);
    }

    public QueryHandler get(QueryKind kind) {
        QueryHandler handler = kind == null ? null : handlers.get(kind);
        if (handler == null) throw new IllegalArgumentException("Handler not found : " + kind);
        return handler;
    }
}
