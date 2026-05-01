package com.kyouseipro.neo.sql.query.recycle;

import java.util.List;
import java.util.Set;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class RecycleQuery {
    public static QueryDefinition recycleList(String category) {

        // ★ 許可カラム（必須：SQLインジェクション対策）
        Set<String> allowed = Set.of(
            "use_date",
            "delivery_date",
            "shipping_date",
            "loss_date"
        );

        if (!allowed.contains(category)) {
            throw new IllegalArgumentException("invalid category: " + category);
        }

        String sql = """
            SELECT r.recycle_id, r.recycle_number, 
                rm.name as maker_name, ri.name as item_name,
                r.use_date, r.delivery_date, r.shipping_date, r.loss_date,
                c.name as company_name, o.name as office_name,
                r.version, r.state
            FROM recycles r
            LEFT OUTER JOIN companies c ON c.company_id = r.company_id AND c.state = ?
            LEFT OUTER JOIN offices o ON o.office_id = r.office_id AND o.state = ?
            LEFT OUTER JOIN recycle_makers rm ON rm.recycle_maker_id = r.maker_id AND rm.state = ?
            LEFT OUTER JOIN recycle_items ri ON ri.recycle_item_id = r.item_id AND ri.state = ?
            WHERE r.state = ? AND r.%s = ?
            """.formatted(category);

        return QueryDefinition.select(
            sql,
            List.of("state", "state", "state", "state", "state", "date")
        );
    }

    public static QueryDefinition recycleDetail() {
        return QueryDefinition.select(
            """
            SELECT r.recycle_id, r.recycle_number,
                r.maker_id, rm.code as maker_code, rm.name as maker_name, 
                r.item_id, ri.code as item_code, ri.name as item_name,
                r.use_date, r.delivery_date, r.shipping_date, r.loss_date,
                r.company_id, r.office_id, c1.name as company_name, o.name as office_name,
                r.recycling_fee, r.disposal_site_id, c2.name as disposal_site_name, r.slip_number, 
                r.version, r.state, r.regist_date, r.update_date
            FROM recycles r
            LEFT OUTER JOIN companies c1 ON c1.company_id = r.company_id AND c1.state = ?
            LEFT OUTER JOIN companies c2 ON c2.company_id = r.disposal_site_id AND c2.state = ?
            LEFT OUTER JOIN offices o ON o.office_id = r.office_id AND o.state = ?
            LEFT OUTER JOIN recycle_makers rm ON rm.recycle_maker_id = r.maker_id AND rm.state = ?
            LEFT OUTER JOIN recycle_items ri ON ri.recycle_item_id = r.item_id AND ri.state = ?
            WHERE r.state = ? AND recycle_id = ?;
            """,
            List.of("state", "state", "state", "state", "state", "state", "recycleId")
        );
    }

    public static QueryDefinition recycleCsv() {

        String sql = """
            SELECT
                recycle_id,
                recycle_number,
                maker_name,
                item_name,
                company_name
            FROM recycles
            WHERE state = ?
            AND recycle_id IN (:ids)
        """;

        List<String> params = List.of("state", "ids");

        List<CsvColumn> columns = List.of(
            new CsvColumn("recycleId", "リサイクルID"),
            new CsvColumn("recycleNumber", "お問合せ管理票番号"),
            new CsvColumn("makerName", "製造業者等名"),
            new CsvColumn("itemName", "品目"),
            new CsvColumn("email", "小売業等名")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}
