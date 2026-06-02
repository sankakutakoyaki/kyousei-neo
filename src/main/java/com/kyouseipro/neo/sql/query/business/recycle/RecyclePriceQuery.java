package com.kyouseipro.neo.sql.query.business.recycle;

import java.util.List;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class RecyclePriceQuery {
    public static QueryDefinition recyclePriceList() {

        QueryDefinition def = QueryDefinition.select(
            """
            SELECT r.*,
                i.recycle_item_id, i.name as item_name,
                p.recycle_price_id, p.price, p.tax_price
            FROM recycle_makers r
            LEFT OUTER JOIN recycle_items i ON i.state = ?
            LEFT OUTER JOIN recycle_prices p ON p.recycle_maker_id = r.recycle_maker_id AND p.recycle_item_id = i.recycle_item_id AND p.state = ?
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
        def.setKind(QueryKind.RECYCLE_PRICE_LIST);
        return def;
    }

    public static QueryDefinition recyclePriceDetail() {
        QueryDefinition def = QueryDefinition.select(
            """
            SELECT
                p.recycle_price_id,
                p.recycle_item_id,
                p.price,
                r.recycle_maker_id,
                r.name
            FROM recycle_makers r
            LEFT JOIN recycle_prices p
                ON p.recycle_maker_id =
                r.recycle_maker_id
            AND p.state = ?
            WHERE r.recycle_maker_id = ?
            AND r.state = ?
            """,
            List.of("state", "recycleMakerId", "state")
        );
        def.setKind(QueryKind.RECYCLE_PRICE_DETAIL);
        return def;
    }

    public static QueryDefinition recyclePriceCsv() {

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
            new CsvColumn("name", "略称"),
            new CsvColumn("itemCode", "コード"),
            new CsvColumn("itemName", "品目"),
            new CsvColumn("price", "料金")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}
