package com.kyouseipro.neo.domain.operations.dispatch.query;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;

@Component("daily_crew_members")
public class DailyCrewMemberLogSqlProvider
        implements LogSqlProvider {

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            daily_crew_member_id INT,
            daily_crew_id INT,
            employee_id INT,
            role INT,
            display_order INT,
            regist_date DATETIME2(7),
            update_date DATETIME2(7),
            version INT,
            state INT
            );
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT
                INSERTED.daily_crew_member_id,
                INSERTED.daily_crew_id,
                INSERTED.employee_id,
                INSERTED.role,
                INSERTED.display_order,
                INSERTED.regist_date,
                INSERTED.update_date,
                INSERTED.version,
                INSERTED.state
            """;
    }

    @Override
    public String buildInsertLog(
            String tableVar,
            String action
    ) {
        return """
            INSERT INTO daily_crew_members_log (
                daily_crew_member_id,
                daily_crew_id,
                employee_id,
                role,
                display_order,
                regist_date,
                update_date,
                version,
                state,
                editor,
                process,
                log_date
            )
            SELECT
                daily_crew_member_id,
                daily_crew_id,
                employee_id,
                role,
                display_order,
                regist_date,
                update_date,
                version,
                state,
                ?,
                ?,
                CURRENT_TIMESTAMP
            FROM %s;
            """.formatted(tableVar);
    }

    @Override
    public List<Object> buildLogParams(
            Map<String, Object> req,
            String action
    ) {
        Object editor = req.get("editor");

        if (editor == null) {
            throw new IllegalArgumentException(
                "editorが設定されていません"
            );
        }

        return List.of(
            editor,
            action
        );
    }
}