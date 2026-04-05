package com.kyouseipro.neo.sql.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class SqlParamBuilder {

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
}