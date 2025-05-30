package com.kyouseipro.neo.entity.person;

import java.time.LocalDate;
import com.kyouseipro.neo.service.DatabaseService;

import lombok.Data;

@Data
public class EmployeeEntity {

    public int company_id;
    public int office_id;
    public String account;
    public int code;
    public int category;
    public String last_name;
    public String first_name;
    public String last_name_kana;
    public String first_name_kana;
    public String phone_number;
    public String postal_code;
    public String full_address;
    public String email;
    public int gender;
    public int blood_type;
    public LocalDate birthday;
    public String emergency_contact;
    public String emergency_contact_number;
    public LocalDate date_of_hire;
    public String user_name;

    // --- 共通ヘルパー ---
    private String escape(String value) {
        return value == null ? "" : value.replace("'", "''");
    }

    // --- ログ用テーブル定義 ---
    public String logTable() {
        return "DECLARE @EmployeeTable TABLE (\n" +
                "    id INT,\n" +
                "    company_id INT,\n" +
                "    office_id INT,\n" +
                "    account VARCHAR(255),\n" +
                "    code INT,\n" +
                "    category INT,\n" +
                "    last_name NVARCHAR(255),\n" +
                "    first_name NVARCHAR(255),\n" +
                "    last_name_kana NVARCHAR(255),\n" +
                "    first_name_kana NVARCHAR(255),\n" +
                "    phone_number VARCHAR(20),\n" +
                "    postal_code VARCHAR(10),\n" +
                "    full_address NVARCHAR(255),\n" +
                "    email VARCHAR(255),\n" +
                "    gender INT,\n" +
                "    blood_type INT,\n" +
                "    birthday DATE,\n" +
                "    emergency_contact NVARCHAR(255),\n" +
                "    emergency_contact_number VARCHAR(20),\n" +
                "    date_of_hire DATE,\n" +
                "    regist_date DATETIME,\n" +
                "    update_date DATETIME,\n" +
                "    version INT,\n" +
                "    state INT\n" +
                ");";
    }

    // --- 共通ログ文字列 ---
    public String logString(String operationType) {
        return " OUTPUT INSERTED.id,\n" +
                "        INSERTED.company_id,\n" +
                "        INSERTED.office_id,\n" +
                "        INSERTED.account,\n" +
                "        INSERTED.code,\n" +
                "        INSERTED.category,\n" +
                "        INSERTED.last_name,\n" +
                "        INSERTED.first_name,\n" +
                "        INSERTED.last_name_kana,\n" +
                "        INSERTED.first_name_kana,\n" +
                "        INSERTED.phone_number,\n" +
                "        INSERTED.postal_code,\n" +
                "        INSERTED.full_address,\n" +
                "        INSERTED.email,\n" +
                "        INSERTED.gender,\n" +
                "        INSERTED.blood_type,\n" +
                "        INSERTED.birthday,\n" +
                "        INSERTED.emergency_contact,\n" +
                "        INSERTED.emergency_contact_number,\n" +
                "        INSERTED.date_of_hire,\n" +
                "        INSERTED.regist_date,\n" +
                "        INSERTED.update_date,\n" +
                "        INSERTED.version,\n" +
                "        INSERTED.state\n" +
                " INTO @EmployeeTable";
    }

    // --- INSERT ---
    public String getInsertString() {
        if (this.birthday == null) {
            this.birthday = LocalDate.of(9999, 12, 31);
        }

        String sql = String.join("\n",
                logTable(),
                "INSERT INTO employees (",
                "    company_id, office_id, account, code, category,",
                "    last_name, first_name, last_name_kana, first_name_kana,",
                "    phone_number, postal_code, full_address, email, gender, blood_type, birthday,",
                "    emergency_contact, emergency_contact_number,",
                "    date_of_hire, regist_date, update_date, version, state",
                ")",
                logString("登録"),
                "VALUES (",
                "    " + company_id + ", " + office_id + ", '" + escape(account) + "', " + code + ", " + category + ",",
                "    '" + escape(last_name) + "', '" + escape(first_name) + "', '" + escape(last_name_kana) + "', '" + escape(first_name_kana) + "',",
                "    '" + escape(phone_number) + "', '" + escape(postal_code) + "', '" + escape(full_address) + "', '" + escape(email) + "',",
                "    " + gender + ", " + blood_type + ", '" + birthday + "',",
                "    '" + escape(emergency_contact) + "', '" + escape(emergency_contact_number) + "',",
                "    '" + date_of_hire + "', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1",
                ");",
                "INSERT INTO employees_log SELECT * FROM @EmployeeTable;",
                DatabaseService.getUpdateLogTableString(user_name, "employees", "登録")
        );
        return sql;
    }

    // --- UPDATE ---
    public String getUpdateString(int id) {
        String sql = String.join("\n",
                logTable(),
                "UPDATE employees",
                logString("更新"),
                "SET",
                "    company_id = " + company_id + ",",
                "    office_id = " + office_id + ",",
                "    account = '" + escape(account) + "',",
                "    code = " + code + ",",
                "    category = " + category + ",",
                "    last_name = '" + escape(last_name) + "',",
                "    first_name = '" + escape(first_name) + "',",
                "    last_name_kana = '" + escape(last_name_kana) + "',",
                "    first_name_kana = '" + escape(first_name_kana) + "',",
                "    phone_number = '" + escape(phone_number) + "',",
                "    postal_code = '" + escape(postal_code) + "',",
                "    full_address = '" + escape(full_address) + "',",
                "    email = '" + escape(email) + "',",
                "    gender = " + gender + ",",
                "    blood_type = " + blood_type + ",",
                "    birthday = '" + birthday + "',",
                "    emergency_contact = '" + escape(emergency_contact) + "',",
                "    emergency_contact_number = '" + escape(emergency_contact_number) + "',",
                "    date_of_hire = '" + date_of_hire + "',",
                "    update_date = CURRENT_TIMESTAMP,",
                "    version = version + 1",
                "WHERE id = " + id + ";",
                "INSERT INTO employees_log SELECT * FROM @EmployeeTable;",
                DatabaseService.getUpdateLogTableString(user_name, "employees", "更新")
        );
        return sql;
    }

    // --- DELETE ---
    public String getDeleteString(int id) {
        String sql = String.join("\n",
                logTable(),
                "DELETE FROM employees",
                logString("削除"),
                "WHERE id = " + id + ";",
                "INSERT INTO employees_log SELECT * FROM @EmployeeTable;",
                DatabaseService.getUpdateLogTableString(user_name, "employees", "削除")
        );
        return sql;
    }
}
