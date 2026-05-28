package com.kyouseipro.neo.sql.query.recycle;

import java.util.List;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class RecycleMakerQuery {

    public static QueryDefinition recycleMakerList() {
        return QueryDefinition.select(
            """
            SELECT r.*, g.name as group_name FROM recycle_makers r
            LEFT OUTER JOIN recycle_groups g ON g.recycle_group_id = [r].[group] AND g.state = ?
            WHERE r.state = ?
            ORDER BY
                CASE WHEN r.kana IS NULL OR r.kana = '' THEN 1 ELSE 0 END,
                r.kana, r.abbr_name,
                CASE
                    WHEN r.name LIKE '(株)%'
                        THEN SUBSTRING(r.name, 4, LEN(r.name))
                    WHEN r.name LIKE '(有)%'
                        THEN SUBSTRING(r.name, 4, LEN(r.name))
                    ELSE r.name
                END
            """,
            List.of("state", "state")
        );
    }

    public static QueryDefinition recycleMakerDetail() {
        return QueryDefinition.select(
            """
            SELECT r.*, p.price, i.code as item_code, i.name as item_name, g.name as group_name FROM recycle_makers r
            LEFT OUTER JOIN recycle_prices p ON p.recycle_maker_id = r.recycle_maker_id AND p.state = ?
            LEFT OUTER JOIN recycle_items i ON i.recycle_item_id = p.recycle_item_id AND i.state = ?
            LEFT OUTER JOIN recycle_groups g ON g.recycle_group_id = [r].[group] AND g.state = ?
            WHERE r.state = ? AND r.recycle_maker_id = ?;
            """,
            List.of("state", "state", "state", "state", "recycleMakerId")
        );
    }

    public static QueryDefinition recycleMakerCsv() {

        String sql = """
            SELECT r.*, p.price, i.code as item_code, i.name as item_name, g.name as group_name FROM recycle_makers r
            LEFT OUTER JOIN recycle_prices p ON p.recycle_maker_id = r.recycle_maker_id AND p.state = ?
            LEFT OUTER JOIN recycle_items i ON i.recycle_item_id = p.recycle_item_id AND i.state = ?
            LEFT OUTER JOIN recycle_groups g ON g.recycle_group_id = [r].[group] AND g.state = ?
            WHERE r.state = ?
            AND r.recycle_maker_id IN (:ids)
            ORDER BY r.kana
        """;

        List<String> params = List.of("state", "state", "state", "state", "ids");

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
