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
            recycle_id INT, recycle_number NVARCHAR(255), 
            maker_id INT, item_id INT, use_date DATE, delivery_date DATE, shipping_date DATE, loss_date DATE,
            company_id INT, office_id INT, recycling_fee INT, disposal_site_id INT, slip_number INT,
            version INT, state INT );
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT INSERTED.recycle_id, INSERTED.recycle_number, INSERTED.maker_id, INSERTED.item_id,
            INSERTED.use_date, INSERTED.delivery_date, INSERTED.shipping_date, INSERTED.loss_date,
            INSERTED.company_id, INSERTED.office_id, INSERTED.recycling_fee, INSERTED.disposal_site_id, INSERTED.slip_number,
            INSERTED.version, INSERTED.state
            """;
    }

    @Override
    public String buildInsertLog(String tableVar, String action) {
        return """
            INSERT INTO recycles_log (
              recycle_id, editor, process, log_date,
              recycle_number, maker_id, item_id, use_date, delivery_date, shipping_date, loss_date,
              company_id, office_id, recycling_fee, disposal_site_id, slip_number, version, state
            )
            SELECT recycle_id, ?, ?, CURRENT_TIMESTAMP,
              recycle_number, maker_id, item_id, use_date, delivery_date, shipping_date, loss_date,
              company_id, office_id, recycling_fee, disposal_site_id, slip_number, version, state
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