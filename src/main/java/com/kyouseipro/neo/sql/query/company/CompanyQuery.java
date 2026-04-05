package com.kyouseipro.neo.sql.query.company;

import java.util.List;

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

    public static QueryDefinition deleteByIds() {
        return QueryDefinition.delete(
            """
            UPDATE companies
            SET state = ?
            WHERE company_id IN (%s)
            """,
            List.of("state", "ids") // ← 特別扱い
        );
    }
}
