package com.kyouseipro.neo.sql.query.personnel.employee;

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
            FROM employees
            WHERE state = ?
            AND employee_id IN (:ids)
        """;

        List<String> params = List.of("state", "ids");

        List<CsvColumn> columns = List.of(
            new CsvColumn("employeeId", "社員ID"),
            new CsvColumn("fullName", "氏名"),
            new CsvColumn("fullNameKana", "カナ"),
            new CsvColumn("phoneNumber", "携帯番号"),
            new CsvColumn("email", "メール")
        );

        return QueryDefinition.csv(sql, params, columns);
    }

    public static QueryDefinition employeeResolve() {
        return QueryDefinition.select(
            """
            SELECT TOP (1)
                e.employee_id,
                e.code,
                e.full_name,
                e.office_id

            FROM employees e

            WHERE e.state = ?
            AND (
                    CONVERT(NVARCHAR(30), e.employee_id) = ?
                    OR CONVERT(NVARCHAR(100), e.code) = ?
                )

            ORDER BY
                CASE
                    WHEN CONVERT(NVARCHAR(30), e.employee_id) = ?
                    THEN 0
                    ELSE 1
                END,
                e.employee_id
            """,
            List.of(
                "state",
                "identifier",
                "identifier",
                "identifier"
            )
        );
    }

    public static QueryDefinition employeeWorkCategoryList() {
        return QueryDefinition.select(
            """
            SELECT
                employee_work_category_id,
                code,
                name,
                display_order
            FROM employee_work_categories
            WHERE state = ?
            ORDER BY
                display_order,
                employee_work_category_id
            """,
            List.of(
                "state"
            )
        );
    }

    public static QueryDefinition employeeWorkCategoryMemberList() {
        return QueryDefinition.select(
            """
            SELECT
                m.employee_work_category_member_id,
                m.employee_id,
                m.employee_work_category_id,
                m.version,
                m.state
            FROM employee_work_category_members m
            WHERE m.employee_id = ?
            ORDER BY
                m.employee_work_category_id
            """,
            List.of(
                "employeeId"
            )
        );
    }
}
