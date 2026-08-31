package com.kyouseipro.neo.domain.business.order.query;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;

@Component("orders")
public class OrderLogSqlProvider implements LogSqlProvider {

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            order_id INT, request_number NVARCHAR(255), 
            visit_date DATE, visit_time NVARCHAR(255),
            prime_constractor_id INT, prime_constractor_office_id INT,
            title NVARCHAR(255), postal_code NVARCHAR(255), full_address NVARCHAR(255), contact_information NVARCHAR(255),
            remarks NVARCHAR(255), complete_date DATETIME2(7),
            version INT, state INT );
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT INSERTED.order_id, INSERTED.request_number,
            INSERTED.visit_date, INSERTED.visit_time,
            INSERTED.prime_constractor_id, INSERTED.prime_constractor_office_id,
            INSERTED.title, INSERTED.postal_code, INSERTED.full_address, INSERTED.contact_information,
            INSERTED.remarks, INSERTED.complete_date,
            INSERTED.version, INSERTED.state
            """;
    }

    @Override
    public String buildInsertLog(String tableVar, String action) {
        return """
            INSERT INTO orders_log (
              order_id, editor, process, log_date,
              request_number, visit_date, visit_time,
              prime_constractor_id, prime_constractor_office_id,
              title, postal_code, full_address, contact_information,
              remarks, complete_date,
              version, state
            )
            SELECT order_id, ?, ?, CURRENT_TIMESTAMP,
              request_number, visit_date, visit_time,
              prime_constractor_id, prime_constractor_office_id,
              title, postal_code, full_address, contact_information,
              remarks, complete_date,
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