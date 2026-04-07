package com.kyouseipro.neo.sql.query.company;

import java.util.List;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class CompanyQuery {

    public static QueryDefinition partnerCompanyList() {
        return QueryDefinition.select(
            """
            SELECT * FROM companies WHERE state = ? AND category = ?;
            """,
            List.of("state", "category")
        );
    }

    public static QueryDefinition partnerCompanyDetail() {
        return QueryDefinition.select(
            """
            SELECT * FROM companies WHERE state = ? AND company_id = ?;
            """,
            List.of("state", "companyId")
        );
    }

    public static QueryDefinition partnerCompanyCsv() {

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
            new CsvColumn("company_id", "会社ID"),
            new CsvColumn("name", "会社名"),
            new CsvColumn("name_kana", "カナ"),
            new CsvColumn("tel_number", "電話番号"),
            new CsvColumn("email", "メール")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}
