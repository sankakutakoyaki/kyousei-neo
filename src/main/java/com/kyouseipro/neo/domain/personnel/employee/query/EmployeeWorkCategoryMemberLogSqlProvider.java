package com.kyouseipro.neo.domain.personnel.employee.query;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;

@Component("employee_work_category_members")
public class EmployeeWorkCategoryMemberLogSqlProvider implements LogSqlProvider {

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            employee_work_category_member_id BIGINT,
            employee_id INT,
            employee_work_category_id BIGINT,
            version INT,
            state INT
            );
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT
                INSERTED.employee_work_category_member_id,
                INSERTED.employee_id,
                INSERTED.employee_work_category_id,
                INSERTED.version,
                INSERTED.state
            """;
    }

    @Override
    public String buildInsertLog(String tableVar, String action) {
        return """
            INSERT INTO employee_work_category_members_log (
                employee_work_category_member_id,
                editor,
                process,
                log_date,
                employee_id,
                employee_work_category_id,
                version,
                state
            )
            SELECT
                employee_work_category_member_id,
                ?,
                ?,
                CURRENT_TIMESTAMP,
                employee_id,
                employee_work_category_id,
                version,
                state
            FROM %s;
            """.formatted(tableVar);
    }

    @Override
    public List<Object> buildLogParams(Map<String,Object> req, String action) {
        Object editor = req.get("editor");
        if(editor == null){
            throw new IllegalArgumentException("editorが設定されていません" );
        }
        return List.of(editor, action);
    }
}