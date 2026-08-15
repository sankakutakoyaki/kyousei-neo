package com.kyouseipro.neo.domain.business.api.order.query;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;

@Component("order_works")
public class OrderWorkLogSqlProvider implements LogSqlProvider {

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            order_work_id INT,
            order_id INT, 
            order_work_code NVARCHAR(255),
            order_work_name NVARCHAR(255),
            order_work_price INT,
            order_work_quantity INT,
            remarks NVARCHAR(255),
            version INT,
            state INT );
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT INSERTED.order_work_id,
            INSERTED.order_id,
            INSERTED.order_work_code,
            INSERTED.order_work_name,
            INSERTED.order_work_price,
            INSERTED.order_work_quantity,
            INSERTED.remarks,
            INSERTED.version,
            INSERTED.state
            """;
    }

    @Override
    public String buildInsertLog(String tableVar, String action) {
        return """
            INSERT INTO order_works_log (
              order_work_id, editor, process, log_date,
              order_id, order_work_code,
              order_work_name, order_work_price,
              order_work_quantity, remarks,
              version, state
            )
            SELECT order_work_id, ?, ?, CURRENT_TIMESTAMP,
              order_id, order_work_code,
              order_work_name, order_work_price,
              order_work_quantity, remarks,
              version, state
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