package com.kyouseipro.neo.sql.query.recycle;

import java.util.List;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class RecycleManufacturerQuery {

    public static QueryDefinition recycleManufacturerList() {
        return QueryDefinition.select(
            """
            SELECT r.*, m.code, m.name as abbr_name, m.kana as abbr_kana, g.name as group_name FROM recycle_manufacturers r
            LEFT OUTER JOIN recycle_makers m ON m.recycle_maker_id = r.recycle_maker_id AND m.state = ?
            LEFT OUTER JOIN recycle_groups g ON g.recycle_group_id = [m].[group] AND g.state = ?
            WHERE r.state = ?
            ORDER BY
                CASE WHEN r.kana IS NULL OR r.kana = '' THEN 1 ELSE 0 END,
                r.kana,
                CASE
                    WHEN r.name LIKE '(株)%'
                        THEN SUBSTRING(r.name, 4, LEN(r.name))
                    WHEN r.name LIKE '(有)%'
                        THEN SUBSTRING(r.name, 4, LEN(r.name))
                    ELSE r.name
                END
            """,
            List.of("state", "state", "state")
        );
    }

    public static QueryDefinition recycleManufacturerDetail() {
        return QueryDefinition.select(
            """
            SELECT r.*, m.code FROM recycle_manufacturers r
            LEFT OUTER JOIN recycle_makers m ON m.recycle_maker_id = r.recycle_maker_id AND m.state = ?
            WHERE r.state = ? AND r.recycle_manufacturer_id = ?;
            """,
            List.of("state", "state", "recycleManufacturerId")
        );
    }

    public static QueryDefinition recycleManufacturerCsv() {

        String sql = """
            SELECT r.*, m.code, m.name as abbr_name, m.kana as abbr_kana, p.price, i.code as item_code, i.name as item_name, g.name as group_name
            FROM recycle_manufacturers r
            LEFT OUTER JOIN recycle_makers m ON m.recycle_maker_id = r.recycle_maker_id AND m.state = ?
            LEFT OUTER JOIN recycle_prices p ON p.recycle_maker_id = r.recycle_maker_id AND p.state = ?
            LEFT OUTER JOIN recycle_items i ON i.recycle_item_id = p.recycle_item_id AND i.state = ?
            LEFT OUTER JOIN recycle_groups g ON g.recycle_group_id = [m].[group] AND g.state = ?
            WHERE r.state = ?
            AND r.recycle_manufacturer_id IN (:ids)
            ORDER BY r.kana
        """;

        List<String> params = List.of("state", "state", "state", "state", "state", "ids");

        List<CsvColumn> columns = List.of(
            new CsvColumn("code", "コード"),
            new CsvColumn("groupName", "グループ"),
            new CsvColumn("name", "製造業者等名"),
            new CsvColumn("abbrName", "略称"),
            new CsvColumn("itemCode", "コード"),
            new CsvColumn("itemName", "品目"),
            new CsvColumn("price", "料金")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}
