package com.kyouseipro.neo.sql.query.staff;

import java.util.List;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class StaffQuery {
    public static QueryDefinition staffList() {
        return QueryDefinition.select(
            """
            SELECT s.staff_id, s.full_name, s.full_name_kana, s.phone_number, s.email,
                c.name as company_name, o.name as office_name FROM staffs s
            INNER JOIN companies c ON c.company_id = s.company_id AND c.state = ? 
            LEFT OUTER JOIN offices o ON o.office_id = s.office_id AND o.state = ?
            WHERE s.state = ?;
            """,
            List.of("state", "state", "state")
        );
    }

    public static QueryDefinition staffDetail() {
        return QueryDefinition.select(
            """
            SELECT s.*, c.name as company_name, o.name as office_name FROM staffs s
            INNER JOIN companies c ON c.company_id = s.company_id AND c.state = ? 
            LEFT OUTER JOIN offices o ON o.office_id = s.office_id AND o.state = ?
            WHERE s.state = ? AND s.staff_id = ?;
            """,
            List.of("state", "state", "state", "staffId")
        );
    }

    public static QueryDefinition staffCsv() {

        String sql = """
            SELECT
                staff_id,
                full_name,
                full_name_kana,
                phone_number,
                email
            FROM staffs
            WHERE state = ?
            AND staff_id IN (:ids)
        """;

        List<String> params = List.of("state", "ids");

        List<CsvColumn> columns = List.of(
            new CsvColumn("staffId", "担当者ID"),
            new CsvColumn("fullName", "氏名"),
            new CsvColumn("fullNameKana", "カナ"),
            new CsvColumn("phoneNumber", "携帯番号"),
            new CsvColumn("email", "メール")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}
