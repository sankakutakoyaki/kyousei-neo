package com.kyouseipro.neo.sql.query.recycle;

import java.util.List;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class RecycleQuery {
    public static QueryDefinition recycleList() {
        return QueryDefinition.select(
            """
            SELECT * FROM recycles WHERE state = ? AND category = ?;
            """,
            List.of("state", "category")
        );
    }

    public static QueryDefinition recycleDetail() {
        return QueryDefinition.select(
            """
            SELECT * FROM recycles WHERE state = ? AND recycle_id = ?;
            """,
            List.of("state", "recycleId")
        );
    }

    public static QueryDefinition recycleCsv() {

        String sql = """
            SELECT
                recycle_id,
                name,
                name_kana,
                tel_number,
                email
            FROM recycles
            WHERE state = ?
            AND recycle_id IN (:ids)
        """;

        List<String> params = List.of("state", "ids");

        List<CsvColumn> columns = List.of(
            new CsvColumn("recycleId", "会社ID"),
            new CsvColumn("name", "会社名"),
            new CsvColumn("nameKana", "カナ"),
            new CsvColumn("telNumber", "電話番号"),
            new CsvColumn("email", "メール")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}
