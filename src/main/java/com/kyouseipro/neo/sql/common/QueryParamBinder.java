package com.kyouseipro.neo.sql.common;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.code.RecycleCategory;
import com.kyouseipro.neo.sql.model.BoundSql;

@Component
public class QueryParamBinder {

    public List<Object> build(
            List<String> paramOrder,
            Map<String, Object> input
    ) {
        List<Object> params = new ArrayList<>();

        for (String key : paramOrder) {
            if (!input.containsKey(key)) {
                throw new IllegalArgumentException("パラメータ不足: " + key);
            }
            // params.add(input.get(key));
            params.add(
                convertValue(
                    key,
                    input.get(key),
                    input
                )
            );
        }

        return params;
    }
    
    public BoundSql bind(
            String sql,
            List<String> paramOrder,
            Map<String, Object> input
    ) {
        List<Object> params = new ArrayList<>();
        for (String key : paramOrder) {
            if (!input.containsKey(key)) {
                throw new IllegalArgumentException("パラメータ不足: " + key);
            }

            Object value = input.get(key);
            if ("ids".equals(key) && value instanceof List<?> list) {
                if (list.isEmpty()) {
                    throw new IllegalArgumentException("idsが空です");
                }
                String placeholders = SqlUtil.placeholders(list.size());
                sql = sql.replace(":ids", placeholders);
                params.addAll(list);
            } else {
                // params.add(value);
                params.add(
                    convertValue(
                        key,
                        value,
                        input
                    )
                );
            }
        }
        return new BoundSql(sql, params);
    }

    private Object convertValue(
            String key,
            Object value,
            Map<String, Object> input
    ) {
        if (value == null) {
            return null;
        }
        if (requiresTimestamp(key, input)) {
            LocalDate d =
                LocalDate.parse(
                    String.valueOf(value)
                );
            return Timestamp.valueOf(
                d.atStartOfDay()
            );
        }
        return value;
    }

    private boolean requiresTimestamp(
            String key,
            Map<String, Object> input
    ) {
        if (!isDateKey(key)) {
            return false;
        }
        return isRegistCategory(input);
    }

    private boolean isDateKey(String key) {
        return "dateFrom".equals(key)
            || "dateTo".equals(key);
    }

    private boolean isRegistCategory(
            Map<String, Object> input
    ) {
        Object cate = input.get("category");
        if (cate == null) {
            return false;
        }
        int code =
            Integer.parseInt(String.valueOf(cate));
        return code ==
            RecycleCategory.REGIST.getCode();
    }
}