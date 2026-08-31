package com.kyouseipro.neo.domain.business.order.query;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;

@Component("order_items")
public class OrderItemLogSqlProvider implements LogSqlProvider {

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            order_item_id INT, order_id INT, 
            arrival_date DATE, jan_code NVARCHAR(255),
            item_maker NVARCHAR(255), item_name NVARCHAR(255), item_model NVARCHAR(255),
            item_quantity INT, item_payment INT, remarks NVARCHAR(255),
            version INT, state INT );
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT INSERTED.order_item_id, INSERTED.order_id,
            INSERTED.arrival_date, INSERTED.jan_code,
            INSERTED.item_maker, INSERTED.item_name, INSERTED.item_model,
            INSERTED.item_quantity, INSERTED.item_payment, INSERTED.remarks,
            INSERTED.version, INSERTED.state
            """;
    }

    @Override
    public String buildInsertLog(String tableVar, String action) {
        return """
            INSERT INTO order_items_log (
              order_item_id, editor, process, log_date,
              order_id, arrival_date, jan_code,
              item_maker, item_name, item_model,
              item_quantity, item_payment, remarks,
              version, state
            )
            SELECT order_item_id, ?, ?, CURRENT_TIMESTAMP,
              order_id, arrival_date, jan_code,
              item_maker, item_name, item_model,
              item_quantity, item_payment, remarks,
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