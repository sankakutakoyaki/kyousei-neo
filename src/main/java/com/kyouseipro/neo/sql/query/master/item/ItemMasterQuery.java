package com.kyouseipro.neo.sql.query.master.item;

import java.util.List;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class ItemMasterQuery {

    public static QueryDefinition itemMasterList() {
        return QueryDefinition.select(
            """
            SELECT item_master_id, jan_code, item_maker, item_name, item_model,
                version, state FROM item_masters
            WHERE state = ?
            """,
            List.of("state")
        );
    }

    public static QueryDefinition itemMasterDetail() {
        return QueryDefinition.select(
            """
            SELECT item_master_id, jan_code, item_maker, item_name, item_model,
                version, state FROM item_masters
            WHERE state = ? AND item_master_id = ?
            """,
            List.of("state", "itemMasterId")
        );
    }

    public static QueryDefinition itemMasterCsv() {

        String sql = """
            SELECT 
                item_master_id,
                jan_code,
                item_maker,
                item_name,
                item_model
            FROM item_masters
            WHERE state = ?
            AND item_master_id IN (:ids)
        """;

        List<String> params = List.of("state", "ids");

        List<CsvColumn> columns = List.of(
            new CsvColumn("itemMasterId", "ID"),
            new CsvColumn("janCode", "JANコード"),
            new CsvColumn("itemMaker", "メーカー"),
            new CsvColumn("itemName", "名称"),
            new CsvColumn("itemModel", "型番")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}
