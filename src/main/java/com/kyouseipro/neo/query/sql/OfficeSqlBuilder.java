package com.kyouseipro.neo.query.sql;

public class OfficeSqlBuilder {

    public static String buildInsertOfficeSql() {
        return
            "DECLARE @InsertedRows TABLE (office_id INT);" +
            "INSERT INTO offices (office_id, office_name, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state) " +
            "OUTPUT INSERTED.office_id INTO @InsertedRows " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);" +
            "INSERT INTO offices_log (office_id, editor, process, log_date, office_id, office_name, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state) " +
            "SELECT o.office_id, ?, 'INSERT', CURRENT_TIMESTAMP, office_id, office_name, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state " +
            "FROM offices o INNER JOIN @InsertedRows ir ON o.office_id = ir.office_id;" +
            "SELECT office_id FROM @InsertedRows;";
    }

    public static String buildUpdateOfficeSql() {
        return
            "DECLARE @UpdatedRows TABLE (office_id INT);" +
            "UPDATE offices SET " +
            "office_id=?, office_name=?, name=?, name_kana=?, tel_number=?, fax_number=?, postal_code=?, full_address=?, email=?, web_address=?, version=?, state=? " +
            "OUTPUT INSERTED.office_id INTO @UpdatedRows " +
            "WHERE office_id=?;" +
            "INSERT INTO offices_log (office_id, editor, process, log_date, office_id, office_name, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state) " +
            "SELECT o.office_id, ?, 'UPDATE', CURRENT_TIMESTAMP, office_id, office_name, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state " +
            "FROM offices o INNER JOIN @UpdatedRows ur ON o.office_id = ur.office_id;" +
            "SELECT office_id FROM @UpdatedRows;";
    }
}
