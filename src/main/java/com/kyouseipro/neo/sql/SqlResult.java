package com.kyouseipro.neo.sql;

import java.util.List;

public class SqlResult {
    private final String sql;
    private final List<Object> params;

    public SqlResult(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public String getSql() { return sql; }
    public List<Object> getParams() { return params; }
}