package com.kyouseipro.neo.domain.business.dispatch;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;

@Component("order_dispatch_assignments")
public class DispatchLogSqlProvider implements LogSqlProvider {
    private static final String COLUMNS = "assignment_id, order_id, employee_id, role, employee_name, assigned_at, assigned_by, cancelled_at, cancelled_by";

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (assignment_id BIGINT, order_id INT, employee_id INT, role VARCHAR(12), "
            + "employee_name NVARCHAR(255), assigned_at DATETIME2, assigned_by NVARCHAR(255), cancelled_at DATETIME2, cancelled_by NVARCHAR(255)); ";
    }
    @Override
    public String buildOutput() {
        return " OUTPUT " + String.join(", ", java.util.Arrays.stream(COLUMNS.split(", ")).map(c -> "INSERTED." + c).toList());
    }
    @Override
    public String buildInsertLog(String tableVar, String action) {
        return " INSERT INTO order_dispatch_assignments_log (" + COLUMNS + ", editor, process, log_date, dispatch_version) "
            + "SELECT " + COLUMNS + ", ?, ?, CURRENT_TIMESTAMP, ? FROM " + tableVar + ";";
    }
    @Override
    public List<Object> buildLogParams(Map<String,Object> req, String action) {
        return List.of(req.get("editor"), action, req.get("dispatchVersion"));
    }
}
