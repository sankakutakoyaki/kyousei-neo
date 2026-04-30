package com.kyouseipro.neo.sql.query.recycle;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.LogSqlProvider;

@Component("recycles")
public class RecycleLogSqlProvider implements LogSqlProvider {

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            company_id INT, category NVARCHAR(255), name NVARCHAR(255), name_kana NVARCHAR(255),
            tel_number NVARCHAR(255), fax_number NVARCHAR(255), postal_code NVARCHAR(255),
            full_address NVARCHAR(255), email NVARCHAR(255), web_address NVARCHAR(255), is_original_price INT,
            version INT, state INT );
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT INSERTED.company_id, INSERTED.category, INSERTED.name, INSERTED.name_kana,
            INSERTED.tel_number, INSERTED.fax_number, INSERTED.postal_code, INSERTED.full_address,
            INSERTED.email, INSERTED.web_address, INSERTED.is_original_price, INSERTED.version, INSERTED.state 
            """;
    }

    @Override
    public String buildInsertLog(String tableVar, String action) {
        return """
            INSERT INTO recycles_log (
            company_id, editor, process, log_date, category, name, name_kana, tel_number, fax_number,
            postal_code, full_address, email, web_address, is_original_price, version, state
            )
            SELECT company_id, ?, ?, CURRENT_TIMESTAMP,
                category, name, name_kana, tel_number, fax_number,
                postal_code, full_address, email, web_address, is_original_price, version, state
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