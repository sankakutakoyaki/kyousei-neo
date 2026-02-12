package com.kyouseipro.neo.corporation.office.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.corporation.office.entity.OfficeEntityRequest;

public class OfficeSqlBuilder {

    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  office_id INT, company_id INT, name NVARCHAR(255), name_kana NVARCHAR(255), " +
            "  tel_number NVARCHAR(255), fax_number NVARCHAR(255), postal_code NVARCHAR(255), " +
            "  full_address NVARCHAR(255), email NVARCHAR(255), web_address NVARCHAR(255), " +
            "  version INT, state INT" +
            "); ";
    }

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.office_id, INSERTED.company_id, INSERTED.name, INSERTED.name_kana, " +
            "INSERTED.tel_number, INSERTED.fax_number, INSERTED.postal_code, INSERTED.full_address, " +
            "INSERTED.email, INSERTED.web_address, INSERTED.version, INSERTED.state ";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
        return
            "INSERT INTO offices_log (" +
            "  office_id, editor, process, log_date, company_id, name, name_kana, tel_number, fax_number, " +
            "  postal_code, full_address, email, web_address, version, state" +
            ") " +
            "SELECT office_id, ?, '" + processName + "', CURRENT_TIMESTAMP, " +
            "  company_id, name, name_kana, tel_number, fax_number, " +
            "  postal_code, full_address, email, web_address, version, state " +
            "FROM " + rowTableName + ";";
    }

    public static String buildBulkInsert(OfficeEntityRequest req) {

        StringBuilder sql = new StringBuilder();
        sql.append(buildLogTable("@InsertedRows"));
        sql.append("INSERT INTO offices (");

        List<String> sets = new ArrayList<>();

        if (req.getCompanyId() != null) {
            sets.add("company_id");
        }

        if (req.getName() != null) {
            sets.add("name");
        }
        if (req.getNameKana() != null) {
            sets.add("name_kana");
        }

        if (req.getTelNumber() != null) {
            sets.add("tel_number");
        }
        if (req.getFaxNumber() != null) {
            sets.add("fax_number");
        }
        if (req.getPostalCode() != null) {
            sets.add("postal_code");
        }
        if (req.getFullAddress() != null) {
            sets.add("full_address");
        }
        if (req.getEmail() != null) {
            sets.add("email");
        }

        if (req.getWebAddress() != null) {
            sets.add("web_address");
        }

        if (sets.isEmpty()) {
            throw new IllegalArgumentException("更新項目がありません");
        }

        sql.append(String.join(", ", sets));
        sql.append(") ");

        sql.append(buildOutputLog() + "INTO @InsertedRows ");

        sql.append("VALUES (");
        sql.append(sets.stream().map(v -> "?").collect(Collectors.joining(",")));
        sql.append(");");

        sql.append(buildInsertLog("@InsertedRows", "INSERT"));
        sql.append("SELECT office_id FROM @InsertedRows;");

        return  sql.toString();
    }

    public static String buildBulkUpdate(OfficeEntityRequest req) {

        StringBuilder sql = new StringBuilder();

        sql.append(buildLogTable("@UpdatedRows"));
        sql.append("UPDATE offices SET ");

        List<String> sets = new ArrayList<>();

        if (req.getCompanyId() != null) {
            sets.add("company_id = ?");
        }

        if (req.getName() != null) {
            sets.add("name = ?");
        }
        if (req.getNameKana() != null) {
            sets.add("name_kana = ?");
        }

        if (req.getTelNumber() != null) {
            sets.add("tel_number = ?");
        }
        if (req.getFaxNumber() != null) {
            sets.add("fax_number = ?");
        }
        if (req.getPostalCode() != null) {
            sets.add("postal_code = ?");
        }
        if (req.getFullAddress() != null) {
            sets.add("full_address = ?");
        }
        if (req.getEmail() != null) {
            sets.add("email = ?");
        }
        if (req.getWebAddress() != null) {
            sets.add("web_address = ?");
        }

        // 共通更新項目
        sets.add("version = version + 1");
        sets.add("update_date = SYSDATETIME() ");

        if (sets.isEmpty()) {
            throw new IllegalArgumentException("更新項目がありません");
        }

        sql.append(String.join(", ", sets));

        sql.append(buildOutputLog() + "INTO @UpdatedRows ");
        sql.append("WHERE office_id = ? AND version = ? AND NOT (state = ?);");
        sql.append(buildInsertLog("@UpdatedRows", "UPDATE"));
        sql.append("SELECT office_id FROM @UpdatedRows;");

        return sql.toString();
    }

    public static String buildDeleteByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count);

        return
            buildLogTable("@Deleted") +

            "UPDATE offices SET state = ? " +
            buildOutputLog() + "INTO @Deleted " +
            "WHERE office_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLog("@Deleted", "DELETE") +
            "SELECT office_id FROM @Deleted;";
    }

    private static String baseSelectString() {
        return
            "SELECT o.*, c.name as company_name, c.name_kana as company_name_kana FROM offices o" + 
            " INNER JOIN companies c ON c.company_id = o.company_id AND NOT (c.state = ?)";
    }

    public static String buildFindById() {
        return
            baseSelectString() +
            " WHERE NOT (o.state = ?) AND o.office_id = ?";
    }

    public static String buildFindAll() {
        return
            baseSelectString() +
            " WHERE NOT (o.state = ?)";
    }

    public static String buildFindAllClient() {
        return 
            baseSelectString() + 
            " WHERE NOT (o.state = ?) AND NOT (c.category = ?)";
    }

    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return
            baseSelectString() +
            " WHERE o.office_id IN (" + placeholders + ") AND NOT (o.state = ?)";
    }
}
