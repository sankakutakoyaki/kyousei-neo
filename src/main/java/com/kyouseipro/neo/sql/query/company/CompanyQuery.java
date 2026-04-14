package com.kyouseipro.neo.sql.query.company;

import java.util.List;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class CompanyQuery {

    public static QueryDefinition companyList() {
        return QueryDefinition.select(
            """
            SELECT * FROM companies WHERE state = ? AND category = ?;
            """,
            List.of("state", "category")
        );
    }

    public static QueryDefinition clientList() {
        return QueryDefinition.select(
            """
            SELECT * FROM companies WHERE state = ? AND NOT(category = 0 OR category = ?);
            """,
            List.of("state", "category")
        );
    }

    public static QueryDefinition companyDetail() {
        return QueryDefinition.select(
            """
            SELECT * FROM companies WHERE state = ? AND company_id = ?;
            """,
            List.of("state", "companyId")
        );
    }

    public static QueryDefinition companyCsv() {

        String sql = """
            SELECT
                company_id,
                name,
                name_kana,
                tel_number,
                email
            FROM companies
            WHERE state = ?
            AND company_id IN (:ids)
        """;

        List<String> params = List.of("state", "ids");

        List<CsvColumn> columns = List.of(
            new CsvColumn("companyId", "会社ID"),
            new CsvColumn("name", "会社名"),
            new CsvColumn("nameKana", "カナ"),
            new CsvColumn("telNumber", "電話番号"),
            new CsvColumn("email", "メール")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}
