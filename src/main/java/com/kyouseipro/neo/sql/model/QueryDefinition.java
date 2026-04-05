package com.kyouseipro.neo.sql.model;

import java.util.List;

import com.kyouseipro.neo.common.Enums.QueryType;
import com.kyouseipro.neo.common.Enums.SqlMode;

import lombok.Data;

@Data
public class QueryDefinition {

    private final String sql;
    private final List<String> paramOrder;
    private final QueryType type;
    private final SqlMode mode;

    private QueryDefinition(String sql, List<String> paramOrder, QueryType type, SqlMode mode) {
        this.sql = sql;
        this.paramOrder = paramOrder;
        this.type = type;
        this.mode = mode;
    }

    public static QueryDefinition select(String sql, List<String> params) {
        return new QueryDefinition(sql, params, QueryType.SELECT, null);
    }

    public static QueryDefinition delete(String sql, List<String> params) {
        return new QueryDefinition(sql, params, QueryType.MODIFY, SqlMode.DELETE);
    }

    public String getSql() { return sql; }
    public List<String> getParamOrder() { return paramOrder; }
    public QueryType getType() { return type; }
    public SqlMode getMode() { return mode; }
}