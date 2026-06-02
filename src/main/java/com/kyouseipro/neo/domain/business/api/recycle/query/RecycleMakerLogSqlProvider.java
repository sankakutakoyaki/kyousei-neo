package com.kyouseipro.neo.domain.business.api.recycle.query;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;

@Component("recycle_makers")
public class RecycleMakerLogSqlProvider implements LogSqlProvider {

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            recycle_maker_id INT, code INT, name NVARCHAR(255), kana NVARCHAR(255), [group] INT,
            version INT, state INT );
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT INSERTED.recycle_maker_id, INSERTED.code, INSERTED.name, INSERTED.kana, INSERTED.[group],
            INSERTED.version, INSERTED.state
            """;
    }

    @Override
    public String buildInsertLog(String tableVar, String action) {
        return """
            INSERT INTO recycle_makers_log (
              recycle_maker_id, editor, process, log_date,
              code, name, kana, [group], version, state
            )
            SELECT recycle_maker_id, ?, ?, CURRENT_TIMESTAMP,
              code, name, kana, [group], version, state
            FROM %s;
            """.formatted(tableVar);
    }

    @Override
    public List<Object> buildLogParams(Map<String, Object> req, String action) {
        Object editor = req.get("editor");
        if (editor == null) {
            throw new IllegalArgumentException("editorが設定されていません");
        }
        return List.of(
            editor,
            action
        );
    }
}