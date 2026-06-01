package com.kyouseipro.neo.sql.query.recycle;

import java.util.List;

import com.kyouseipro.neo.sql.model.QueryDefinition;

public class RecycleItemQuery {
    public static QueryDefinition recycleItemList() {

        return QueryDefinition.select(

            """

            SELECT

                recycle_item_id,

                code,

                name

            FROM recycle_items

            WHERE state = ?

            ORDER BY display_order

            """,

            List.of("state")

        );

    }
}
