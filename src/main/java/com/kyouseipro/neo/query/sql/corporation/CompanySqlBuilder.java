package com.kyouseipro.neo.query.sql.corporation;

import com.kyouseipro.neo.common.Utilities;

public class CompanySqlBuilder {

    public static String buildInsertCompanySql() {
        return
            "DECLARE @InsertedRows TABLE (company_id INT);" +
            "INSERT INTO company (category, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state) " +
            "OUTPUT INSERTED.company_id INTO @InsertedRows " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);" +

            "INSERT INTO company_log (company_id, editor, process, log_date, category, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state) " +
            "SELECT c.company_id, ?, 'INSERT', CURRENT_TIMESTAMP, c.category, c.name, c.name_kana, c.tel_number, c.fax_number, c.postal_code, c.full_address, c.email, c.web_address, c.version, c.state " +
            "FROM company c INNER JOIN @InsertedRows ir ON c.company_id = ir.company_id;" +

            "SELECT company_id FROM @InsertedRows;";
    }

    public static String buildUpdateCompanySql() {
        return
            "DECLARE @UpdatedRows TABLE (company_id INT);" +
            "UPDATE company SET " +
            "category=?, name=?, name_kana=?, tel_number=?, fax_number=?, postal_code=?, full_address=?, email=?, web_address=?, version=?, state=? " +
            "OUTPUT INSERTED.company_id INTO @UpdatedRows " +
            "WHERE company_id=?;" +

            "INSERT INTO company_log (company_id, editor, process, log_date, category, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state) " +
            "SELECT c.company_id, ?, 'UPDATE', CURRENT_TIMESTAMP, c.category, c.name, c.name_kana, c.tel_number, c.fax_number, c.postal_code, c.full_address, c.email, c.web_address, c.version, c.state " +
            "FROM company c INNER JOIN @UpdatedRows ur ON c.company_id = ur.company_id;" +

            "SELECT company_id FROM @UpdatedRows;";
    }

    public static String buildFindByIdSql() {
        return "SELECT * FROM companies WHERE company_id = ? AND NOT (state = ?)";
    }

    public static String buildFindAllSql() {
        return "SELECT * FROM companies WHERE NOT (state = ?)";
    }

    public static String buildFindAllClientSql() {
        return "SELECT * FROM companies WHERE NOT (category = ?) AND NOT (state = ?)";
    }

    public static String buildDeleteCompanyForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return
            "DECLARE @DeletedRows TABLE (company_id INT); " +

            "UPDATE companies " +
            "SET state = ? " +
            "OUTPUT INSERTED.company_id INTO @DeletedRows " +
            "WHERE company_id IN (" + placeholders + ") " +
            "AND NOT (state = ?); " +

            "INSERT INTO company_log " +
            "(company_id, editor, process, log_date, category, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state) " +
            "SELECT c.company_id, ?, 'DELETE', CURRENT_TIMESTAMP, " +
            "c.category, c.name, c.name_kana, c.tel_number, c.fax_number, c.postal_code, " +
            "c.full_address, c.email, c.web_address, c.version, c.state " +
            "FROM company c INNER JOIN @DeletedRows d ON c.company_id = d.company_id; ";
    }

    public static String buildDownloadCsvCompanyForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return "SELECT * FROM companies WHERE company_id IN (" + placeholders + ") \" + NOT (state = ?)";
    }
}
