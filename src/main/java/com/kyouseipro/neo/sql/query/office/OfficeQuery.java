package com.kyouseipro.neo.sql.query.office;

import java.util.List;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class OfficeQuery {

    public static QueryDefinition officeList() {
        return QueryDefinition.select(
            """
            SELECT * FROM offices            
            WHERE state = ?;            
            """,
            List.of("state")
        );
    }

    public static QueryDefinition clientOfficeList() {
        return QueryDefinition.select(
            """
            SELECT o.*, c.name as company_name  FROM offices o
            LEFT OUTER JOIN companies c ON c.company_id = o.company_id AND c.state = ?
            WHERE o.state = ? AND NOT(c.category = 0 OR c.category = ?);
            """,
            List.of("state", "state", "category")
        );
    }

    public static QueryDefinition officeDetail() {
        return QueryDefinition.select(
            """
            SELECT * FROM offices WHERE state = ? AND office_id = ?;
            """,
            List.of("state", "officeId")
        );
    }

    public static QueryDefinition officeCsv() {

        String sql = """
            SELECT
                office_id,
                name,
                name_kana,
                tel_number,
                email
            FROM offices
            WHERE state = ?
            AND office_id IN (:ids)
        """;

        List<String> params = List.of("state", "ids");

        List<CsvColumn> columns = List.of(
            new CsvColumn("officeId", "支店ID"),
            new CsvColumn("name", "支店名"),
            new CsvColumn("nameKana", "カナ"),
            new CsvColumn("telNumber", "電話番号"),
            new CsvColumn("email", "メール")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}