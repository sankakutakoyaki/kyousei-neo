package com.kyouseipro.neo.sql.model;

import java.util.LinkedHashMap;

public class Row extends LinkedHashMap<String, Object> {

    public String getString(String key) {
        return (String) get(key);
    }

    public Integer getInt(String key) {
        Object v = get(key);
        return v == null ? null : ((Number) v).intValue();
    }
}