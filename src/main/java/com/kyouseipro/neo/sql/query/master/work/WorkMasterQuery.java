package com.kyouseipro.neo.sql.query.master.work;

import java.util.List;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class WorkMasterQuery {

    public static QueryDefinition workMasterList() {
        return QueryDefinition.select(
            """
            SELECT work_master_id, work_code, work_name, work_price,
                version, state FROM work_masters
            WHERE state = ?
            """,
            List.of("state")
        );
    }

    public static QueryDefinition workMasterDetail() {
        return QueryDefinition.select(
            """
            SELECT work_master_id, work_code, work_name, work_price,
                version, state FROM work_masters
            WHERE state = ? AND work_master_id = ?
            """,
            List.of("state", "workMasterId")
        );
    }

    public static QueryDefinition workMasterCsv() {

        String sql = """
            SELECT 
                work_master_id,
                work_code,
                work_name,
                work_price
            FROM work_masters
            WHERE state = ?
            AND work_master_id IN (:ids)
        """;

        List<String> params = List.of("state", "ids");

        List<CsvColumn> columns = List.of(
            new CsvColumn("workMasterId", "ID"),
            new CsvColumn("workCode", "コード"),
            new CsvColumn("workName", "作業名"),
            new CsvColumn("workPrice", "金額")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}
