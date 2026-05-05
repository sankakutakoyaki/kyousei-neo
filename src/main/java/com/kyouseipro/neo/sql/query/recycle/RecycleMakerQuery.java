package com.kyouseipro.neo.sql.query.recycle;

import java.util.List;

import com.kyouseipro.neo.sql.model.QueryDefinition;

public class RecycleMakerQuery {

    public static QueryDefinition recycleMakerList() {
        return QueryDefinition.select(
            """
            SELECT * FROM recycle_makers WHERE state = ?;
            """,
            List.of("state")
        );
    }
}
