package com.kyouseipro.neo.domain.business.api.recycle.query;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;

@Component("recycle_prices")
public class RecyclePriceLogSqlProvider implements LogSqlProvider {

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            recycle_price_id INT, recycle_maker_id INT, recycle_item_id INT, price INT, tax_price INT,
            version INT, state INT );
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT INSERTED.recycle_price_id, INSERTED.recycle_maker_id, INSERTED.recycle_item_id, INSERTED.price, INSERTED.tax_price,
            INSERTED.version, INSERTED.state
            """;
    }

    @Override
    public String buildInsertLog(String tableVar, String action) {
        return """
            INSERT INTO recycle_prices_log (
              recycle_price_id, editor, process, log_date,
              recycle_maker_id, recycle_item_id, price, tax_price, version, state
            )
            SELECT recycle_price_id, ?, ?, CURRENT_TIMESTAMP,
              recycle_maker_id, recycle_item_id, price, tax_price, version, state
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