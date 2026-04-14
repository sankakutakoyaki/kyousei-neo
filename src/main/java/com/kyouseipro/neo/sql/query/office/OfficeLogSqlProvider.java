package com.kyouseipro.neo.sql.query.office;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.LogSqlProvider;

@Component("offices")
public class OfficeLogSqlProvider implements LogSqlProvider{

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            office_id INT, company_id INT, name NVARCHAR(255), name_kana NVARCHAR(255),
            tel_number NVARCHAR(255), fax_number NVARCHAR(255), postal_code NVARCHAR(255),
            full_address NVARCHAR(255), email NVARCHAR(255), web_address NVARCHAR(255),
            version INT, state INT );                
            """;
    }

    @Override
    public  String buildOutput() {
        return """
            OUTPUT INSERTED.office_id, INSERTED.company_id, INSERTED.name, INSERTED.name_kana,
            INSERTED.tel_number, INSERTED.fax_number, INSERTED.postal_code, INSERTED.full_address,
            INSERTED.email, INSERTED.web_address, INSERTED.version, INSERTED.state
            """;
    }

    @Override
    public  String buildInsertLog(String tableVar, String action) {
        return """
            INSERT INTO offices_log (
              office_id, editor, process, log_date, company_id, name, name_kana, tel_number, fax_number,
              postal_code, full_address, email, web_address, version, state
            )
            SELECT office_id, ?, ?, CURRENT_TIMESTAMP,
              company_id, name, name_kana, tel_number, fax_number,
              postal_code, full_address, email, web_address, version, state 
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
