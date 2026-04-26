package com.kyouseipro.neo.sql.query.employee;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.interfaces.LogSqlProvider;

@Component("employees")
public class EmployeeLogSqlProvider implements LogSqlProvider {

    @Override
    public String buildLogTable(String tableVar) {
        return "DECLARE " + tableVar + " TABLE (" + """
            employee_id INT, company_id INT, office_id INT, account NVARCHAR(255), code NVARCHAR(255), category NVARCHAR(255),
            last_name NVARCHAR(255), first_name NVARCHAR(255), last_name_kana NVARCHAR(255), first_name_kana NVARCHAR(255),
            phone_number NVARCHAR(255), postal_code NVARCHAR(255), full_address NVARCHAR(255), email NVARCHAR(255),
            gender NVARCHAR(255), blood_type NVARCHAR(255), birthday DATE, emergency_contact NVARCHAR(255),
            emergency_contact_number NVARCHAR(255), date_of_hire DATE, version INT, state INT);
            """;
    }

    @Override
    public String buildOutput() {
        return """
            OUTPUT INSERTED.employee_id, INSERTED.company_id, INSERTED.office_id, INSERTED.account, INSERTED.code, INSERTED.category,
            INSERTED.last_name, INSERTED.first_name, INSERTED.last_name_kana, INSERTED.first_name_kana,
            INSERTED.phone_number, INSERTED.postal_code, INSERTED.full_address, INSERTED.email,
            INSERTED.gender, INSERTED.blood_type, INSERTED.birthday, INSERTED.emergency_contact,
            INSERTED.emergency_contact_number, INSERTED.date_of_hire, INSERTED.version, INSERTED.state
            """;
    }

    @Override
    public String buildInsertLog(String tableVar, String action) {
        return """
            INSERT INTO employees_log (
            employee_id, editor, process, log_date, company_id, office_id, account, code, category,
            last_name, first_name, last_name_kana, first_name_kana, phone_number, postal_code, full_address,
            email, gender, blood_type, birthday, emergency_contact, emergency_contact_number, date_of_hire, version, state
            )
            SELECT employee_id, ?, ?, CURRENT_TIMESTAMP, company_id, office_id, account, code, category,
                last_name, first_name, last_name_kana, first_name_kana, phone_number, postal_code, full_address,
                email, gender, blood_type, birthday, emergency_contact, emergency_contact_number, date_of_hire, version, state
            FROM %s;
            """.formatted(tableVar);
    }

    @Override
    public List<Object> buildLogParams(Map<String, Object> req, String action) {
        Object editor = req.get("editor");
        if (editor == null) {
            throw new IllegalArgumentException("editorが設定されていません");
        }
        return List.of(
            editor,
            action
        );
    }
}
