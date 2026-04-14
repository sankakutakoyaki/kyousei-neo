package com.kyouseipro.neo.sql.query.employee;

import java.util.List;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;

public class EmployeeQuery {

    public static QueryDefinition employeeList() {
        return QueryDefinition.select(
            """
            SELECT e.employee_id, e.account, e.code, e.category, e.full_name, e.full_name_kana,
                e.phone_number, e.company_id, e.office_id,
                COALESCE(c.name, '') as company_name, COALESCE(o.name, '') as office_name,
                e.version, e.state FROM employees e
            LEFT OUTER JOIN companies c ON c.company_id = e.company_id AND c.state = ?
            LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND o.state = ?
            WHERE e.state = ? AND e.category = ?
            """,
            List.of("state", "state", "state", "category")
        );
    }

    public static QueryDefinition employeeDetail() {
        return QueryDefinition.select(
            """
            SELECT e.employee_id, e.account, e.code, e.category,
                e.first_name, e.last_name, e.full_name, e.first_name_kana, e.last_name_kana, e.full_name_kana,
                e.phone_number, e.postal_code, e.full_address, e.company_id, e.office_id,
                COALESCE(c.name, '') as company_name, COALESCE(o.name, '') as office_name,
                e.email, e.gender, e.blood_type, e.birthday, e.emergency_contact, e.emergency_contact_number, e.date_of_hire,
                e.version, e.state FROM employees e
            LEFT OUTER JOIN companies c ON c.company_id = e.company_id AND c.state = ?
            LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND o.state = ?
            WHERE e.state = ? AND e.employee_id = ?
            """,
            List.of("state", "state", "state", "employeeId")
        );
    }

    public static QueryDefinition employeeCsv() {

        String sql = """
            SELECT
                employee_id,
                full_name,
                full_name_kana,
                phone_number,
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
            new CsvColumn("telNumber", "携帯番号"),
            new CsvColumn("email", "メール")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}
