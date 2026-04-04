package com.kyouseipro.neo.abstracts;

import java.util.Map;

import com.kyouseipro.neo.common.Enums.SqlMode;
import com.kyouseipro.neo.dto.sql.SqlBuilder;
import com.kyouseipro.neo.dto.sql.SqlResult;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.interfaces.LogSqlProvider;

public abstract class BaseRepository {

    protected final SqlRepository sqlRepository;
    protected final LogSqlProvider logProvider;

    protected BaseRepository(SqlRepository sqlRepository, LogSqlProvider logProvider) {
        this.sqlRepository = sqlRepository;
        this.logProvider = logProvider;
    }

    protected Long insert(String table, Map<String,Object> req, String editor, String idKey){
        req.put("editor", editor);

        SqlResult result = SqlBuilder.buildSqlWithLog(
            table,
            req,
            SqlMode.INSERT,
            idKey,
            "version",
            logProvider
        );

        return sqlRepository.insert(
            result.getSql(),
            result.getParams(),
            rs -> rs.getLong(toSnake(idKey))
        );
    }

    protected int update(String table, Map<String,Object> req, String editor, String idKey){
        req.put("editor", editor);

        SqlResult result = SqlBuilder.buildSqlWithLog(
            table,
            req,
            SqlMode.UPDATE,
            idKey,
            "version",
            logProvider
        );

        return sqlRepository.updateRequired(
            result.getSql(),
            result.getParams(),
            "更新に失敗しました"
        );
    }

    protected int delete(String table, Map<String,Object> req, String editor, String idKey){
        req.put("editor", editor);

        SqlResult result = SqlBuilder.buildSqlWithLog(
            table,
            req,
            SqlMode.DELETE,
            idKey,
            "version",
            logProvider
        );

        return sqlRepository.updateRequired(
            result.getSql(),
            result.getParams(),
            "削除に失敗しました"
        );
    }

    private String toSnake(String camel){
        return camel.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}