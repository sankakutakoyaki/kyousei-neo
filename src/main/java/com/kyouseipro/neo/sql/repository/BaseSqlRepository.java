package com.kyouseipro.neo.sql.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.enums.system.SqlMode;
import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;
import com.kyouseipro.neo.sql.common.SqlBuilder;
import com.kyouseipro.neo.sql.model.SqlResult;
import com.kyouseipro.neo.sql.model.TableMeta;
import com.kyouseipro.neo.sql.provider.LogSqlProviderResolver;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BaseSqlRepository {

    protected final SqlRepository sqlRepository;
    private final LogSqlProviderResolver resolver;

    public Long insert(
            TableMeta meta,
            Map<String,Object> req,
            String editor
    ){
        req.put("editor", editor);
        LogSqlProvider logProvider = resolver.resolve(meta.tableName());
        SqlResult result = SqlBuilder.buildSqlWithLog(
            meta.tableName(),
            req,
            SqlMode.INSERT,
            meta.idColumn(),
            meta.versionColumn(),
            logProvider
        );
        return sqlRepository.insert(
            result.getSql(),
            result.getParams(),
            rs -> rs.getLong(toSnake(meta.idColumn()))
        );
    }

    public int update(
            TableMeta meta,
            Map<String,Object> req,
            String editor
    ){
        req.put("editor", editor);
        LogSqlProvider logProvider = resolver.resolve(meta.tableName());
        SqlResult result = SqlBuilder.buildSqlWithLog(
            meta.tableName(),
            req,
            SqlMode.UPDATE,
            meta.idColumn(),
            meta.versionColumn(),
            logProvider
        );
        return sqlRepository.updateRequired(
            result.getSql(),
            result.getParams(),
            "更新に失敗しました"
        );
    }

    public int updateByIds(
            TableMeta meta,
            List<?> ids,
            Map<String,Object> req,
            String editor
    ){
        req.put("editor", editor);
        LogSqlProvider logProvider = resolver.resolve(meta.tableName());
        SqlResult result = SqlBuilder.buildUpdateByIds(
            meta,
            ids,
            req,
            editor,
            logProvider
        );
        return sqlRepository.updateRequired(
            result.getSql(),
            result.getParams(),
            "更新に失敗しました"
        );
    }

    public int delete(
            TableMeta meta,
            Map<String,Object> req,
            String editor
    ){
        req.put("editor", editor);
        LogSqlProvider logProvider = resolver.resolve(meta.tableName());
        SqlResult result = SqlBuilder.buildSqlWithLog(
            meta.tableName(),
            req,
            SqlMode.DELETE,
            meta.idColumn(),
            meta.versionColumn(),
            logProvider
        );
        return sqlRepository.updateRequired(
            result.getSql(),
            result.getParams(),
            "削除に失敗しました"
        );
    }

    public int deleteByIds(
            TableMeta meta,
            List<?> ids,
            String editor
    ){
        LogSqlProvider logProvider = resolver.resolve(meta.tableName());
        SqlResult result = SqlBuilder.buildDeleteByIds(
            meta,
            ids,
            editor,
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