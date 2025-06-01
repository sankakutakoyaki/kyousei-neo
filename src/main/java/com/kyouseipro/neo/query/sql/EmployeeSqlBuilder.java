package com.kyouseipro.neo.query.sql;

// import java.util.List;

// import com.kyouseipro.neo.common.Enums;
// import com.kyouseipro.neo.entity.person.EmployeeEntity;
// import com.kyouseipro.neo.interfaceis.Entity;
// import com.kyouseipro.neo.service.FileService;

// import lombok.RequiredArgsConstructor;

// @RequiredArgsConstructor
public class EmployeeSqlBuilder {
    // private final FileService fileService;

    public static String buildInsertEmployeeSql() {
        return
            "DECLARE @InsertedRows TABLE (employee_id INT); " +
            "INSERT INTO employees (employe_id, office_id, account, code, category, " +
            "last_name, first_name, full_name, last_name_kana, first_name_kana, full_name_kana, " +
            "phone_number, postal_code, full_address, email, gender, blood_type, birthday, emergency_contact, " +
            "emergency_contact_number, date_of_hire, version, state) " +
            "OUTPUT INSERTED.employee_id INTO @InsertedRows " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +
            "INSERT INTO employees_log (employee_id, editor, process, log_date, " +
            "employe_id, office_id, account, code, category, last_name, first_name, full_name, last_name_kana, first_name_kana, full_name_kana, " +
            "phone_number, postal_code, full_address, email, gender, blood_type, birthday, emergency_contact, emergency_contact_number, date_of_hire, version, state) " +
            "SELECT e.employee_id, ?, 'INSERT', CURRENT_TIMESTAMP, " +
            "e.employe_id, e.office_id, e.account, e.code, e.category, e.last_name, e.first_name, e.full_name, e.last_name_kana, e.first_name_kana, e.full_name_kana, " +
            "e.phone_number, e.postal_code, e.full_address, e.email, e.gender, e.blood_type, e.birthday, e.emergency_contact, e.emergency_contact_number, e.date_of_hire, e.version, e.state " +
            "FROM employees e " +
            "INNER JOIN @InsertedRows ir ON e.employee_id = ir.employee_id; " +
            "SELECT employee_id FROM @InsertedRows;";
    }

    public static String buildUpdateEmployeeSql() {
        return
            "DECLARE @UpdatedRows TABLE (employee_id INT); " +
            "UPDATE employees SET " +
            "employe_id=?, office_id=?, account=?, code=?, category=?, " +
            "last_name=?, first_name=?, full_name=?, last_name_kana=?, first_name_kana=?, full_name_kana=?, " +
            "phone_number=?, postal_code=?, full_address=?, email=?, gender=?, blood_type=?, birthday=?, emergency_contact=?, " +
            "emergency_contact_number=?, date_of_hire=?, version=?, state=? " +
            "OUTPUT INSERTED.employee_id INTO @UpdatedRows " +
            "WHERE employee_id=?; " +
            "INSERT INTO employees_log (employee_id, editor, process, log_date, " +
            "employe_id, office_id, account, code, category, last_name, first_name, full_name, last_name_kana, first_name_kana, full_name_kana, " +
            "phone_number, postal_code, full_address, email, gender, blood_type, birthday, emergency_contact, emergency_contact_number, date_of_hire, version, state) " +
            "SELECT e.employee_id, ?, 'UPDATE', CURRENT_TIMESTAMP, " +
            "e.employe_id, e.office_id, e.account, e.code, e.category, e.last_name, e.first_name, e.full_name, e.last_name_kana, e.first_name_kana, e.full_name_kana, " +
            "e.phone_number, e.postal_code, e.full_address, e.email, e.gender, e.blood_type, e.birthday, e.emergency_contact, e.emergency_contact_number, e.date_of_hire, e.version, e.state " +
            "FROM employees e " +
            "INNER JOIN @UpdatedRows ur ON e.employee_id = ur.employee_id; " +
            "SELECT employee_id FROM @UpdatedRows;";
    }

    // public String getCsvString(List<Entity> items) {
    //     StringBuilder sb = new StringBuilder();

    //     // ヘッダー
    //     sb.append("ID,");
    //     sb.append("コード,");
    //     sb.append("姓,");
    //     sb.append("名,");
    //     sb.append("せい,");
    //     sb.append("めい,");
    //     sb.append("携帯番号,");
    //     sb.append("郵便番号,");
    //     sb.append("住所,");
    //     sb.append("メールアドレス,");
    //     sb.append("性別,");
    //     sb.append("血液型,");
    //     sb.append("生年月日,");
    //     sb.append("緊急連絡先,");
    //     sb.append("緊急連絡先番号");
    //     sb.append("\n");

    //     for (Entity item : items) {
    //         EmployeeEntity entity = (EmployeeEntity) item;
    //         sb.append(fileService.toCsvField(entity.getEmployee_id()));
    //         sb.append(fileService.toCsvField(entity.getCode()));
    //         sb.append(fileService.toCsvField(entity.getLast_name()));
    //         sb.append(fileService.toCsvField(entity.getFirst_name()));
    //         sb.append(fileService.toCsvField(entity.getLast_name_kana()));
    //         sb.append(fileService.toCsvField(entity.getFirst_name_kana()));
    //         sb.append(fileService.toCsvField(entity.getPhone_number()));
    //         sb.append(fileService.toCsvField(entity.getPostal_code()));
    //         sb.append(fileService.toCsvField(entity.getFull_address()));
    //         sb.append(fileService.toCsvField(entity.getEmail()));
    //         sb.append(fileService.toCsvField(Enums.gender.getDescriptionByNum(entity.getGender())));
    //         sb.append(fileService.toCsvField(Enums.bloodType.getDescriptionByNum(entity.getBlood_type())));
    //         sb.append(fileService.toCsvField(entity.getBirthday()));
    //         sb.append(fileService.toCsvField(entity.getEmergency_contact()));
    //         sb.append(fileService.toCsvField(entity.getEmergency_contact_number()));
    //         sb.append("\n");
    //     }

    //     return sb.toString();
    // }
}
