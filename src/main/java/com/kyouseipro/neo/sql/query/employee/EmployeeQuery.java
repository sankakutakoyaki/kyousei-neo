package com.kyouseipro.neo.sql.query.employee;

import java.util.List;

import com.kyouseipro.neo.sql.model.QueryDefinition;

public class EmployeeQuery {

    public static QueryDefinition partnerEmployeeList() {
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
            WHERE e.state = ? AND e.category = ?
            """,
            List.of("companyState", "officeState", "employeeState", "category")
        );
    }

    public static QueryDefinition partnerEmployeeDetail() {
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
            List.of("companyState", "officeState", "employeeState", "employeeId")
        );
    }
}
