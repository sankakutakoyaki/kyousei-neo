package com.kyouseipro.neo.domain.business.recycle.query;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;

@Component("recycle_manufacturers")
public class RecycleManufacturerLogSqlProvider implements LogSqlProvider {

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            recycle_manufacturer_id INT, recycle_maker_id INT, name NVARCHAR(255), kana NVARCHAR(255),
            version INT, state INT );
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT INSERTED.recycle_manufacturer_id, INSERTED.recycle_maker_id, INSERTED.name, INSERTED.kana,
            INSERTED.version, INSERTED.state
            """;
    }

    @Override
    public String buildInsertLog(String tableVar, String action) {
        return """
            INSERT INTO recycle_manufacturers_log (
              recycle_manufacturer_id, editor, process, log_date,
              recycle_maker_id, name, kana, version, state
            )
            SELECT recycle_manufacturer_id, ?, ?, CURRENT_TIMESTAMP,
              recycle_maker_id, name, kana, version, state
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