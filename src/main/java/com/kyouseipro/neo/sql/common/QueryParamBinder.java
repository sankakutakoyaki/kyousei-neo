package com.kyouseipro.neo.sql.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

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
            params.add(input.get(key));
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
                params.add(value);
            }
        }

        return new BoundSql(sql, params);
    }
}