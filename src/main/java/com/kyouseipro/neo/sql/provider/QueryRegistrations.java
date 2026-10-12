package com.kyouseipro.neo.sql.provider;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.util.ClassUtils;

import com.kyouseipro.neo.common.enums.system.QueryId;

final class QueryRegistrations {
    private QueryRegistrations() {}

    static <P, V> Map<QueryId, V> collect(List<P> parts, Function<P, Map<QueryId, V>> definitions) {
        Map<QueryId, V> values = new EnumMap<>(QueryId.class);
        Map<QueryId, String> owners = new EnumMap<>(QueryId.class);
        for (P part : parts) {
            String owner = ClassUtils.getUserClass(part).getName();
            definitions.apply(part).forEach((id, value) -> {
                Objects.requireNonNull(id, "queryIdがnullです: " + owner);
                Objects.requireNonNull(value, "query定義がnullです: " + id + " / " + owner);
                String previous = owners.putIfAbsent(id, owner);
                if (previous != null) {
                    throw new IllegalStateException("queryIdの重複登録: " + id.getId()
                        + " / " + previous + " / " + owner);
                }
                values.put(id, value);
            });
        }
        return Map.copyOf(values);
    }
}
