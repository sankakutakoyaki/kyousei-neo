package com.kyouseipro.neo.sql.model;

import java.util.List;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.QueryType;
// import com.kyouseipro.neo.common.Enums.SqlMode;

import lombok.Data;

@Data
public class QueryDefinition {

    private String sql;
    private List<String> paramOrder;
    private QueryType type;

    private QueryKind kind;
    private TableMeta tableMeta;

    private List<CsvColumn> csvColumns;

    private QueryDefinition(String sql, List<String> paramOrder, QueryType type) {
        this.sql = sql;
        this.paramOrder = paramOrder;
        this.type = type;
        this.kind = QueryKind.SQL;
    }
    
    // ★ SQL用コンストラクタ
    public QueryDefinition(QueryType type, String sql, List<String> paramOrder) {
        this.type = type;
        this.sql = sql;
        this.paramOrder = paramOrder;
        this.kind = QueryKind.SQL;
    }

    // ★ DELETE用コンストラクタ（今回必要）
    public QueryDefinition(QueryType type, QueryKind kind, TableMeta tableMeta) {
        this.type = type;
        this.kind = kind;
        this.tableMeta = tableMeta;
    }

    public static QueryDefinition select(String sql, List<String> params) {
        return new QueryDefinition(sql, params, QueryType.SELECT);
    }

    public static QueryDefinition delete(String sql, List<String> params) {
        return new QueryDefinition(sql, params, QueryType.UPDATE);
    }

    public static QueryDefinition csv(String sql, List<String> params, List<CsvColumn> columns) {
        QueryDefinition def = new QueryDefinition(QueryType.SELECT, sql, params);
        def.setKind(QueryKind.CSV);
        def.setCsvColumns(columns);
        return def;
    }

    public String getSql() { return sql; }
    public List<String> getParamOrder() { return paramOrder; }
    public QueryType getType() { return type; }
}