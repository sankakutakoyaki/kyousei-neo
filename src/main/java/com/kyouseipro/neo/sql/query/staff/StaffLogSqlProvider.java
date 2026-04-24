package com.kyouseipro.neo.sql.query.staff;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.LogSqlProvider;

@Component("staffs")
public class StaffLogSqlProvider implements LogSqlProvider {

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            staff_id INT, company_id INT, office_id INT,
            last_name NVARCHAR(255), first_name NVARCHAR(255),
            last_name_kana NVARCHAR(255), first_name_kana NVARCHAR(255),
            phone_number NVARCHAR(255), email NVARCHAR(255),
            version INT, state INT);
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT INSERTED.staff_id, INSERTED.company_id, INSERTED.office_id,
            INSERTED.last_name, INSERTED.last_name_kana, INSERTED.first_name, INSERTED.first_name_kana,
            INSERTED.phone_number, INSERTED.email, INSERTED.version, INSERTED.state
            """;
    }

    @Override
    public String buildInsertLog(String tableVar, String action) {
        return """
            INSERT INTO staffs_log (
            staff_id, editor, process, log_date, company_id, office_id,
            last_name, last_name_kana, first_name, first_name_kana, phone_number, email, version, state
            )
            SELECT staff_id, ?, ?, CURRENT_TIMESTAMP,
                 company_id, office_id, last_name, last_name_kana, first_name, first_name_kana, phone_number, email, version, state
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
