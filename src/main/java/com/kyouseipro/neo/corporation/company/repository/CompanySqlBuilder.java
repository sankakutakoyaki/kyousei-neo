package com.kyouseipro.neo.corporation.company.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.corporation.company.dto.CompanyRequest;
import com.kyouseipro.neo.dto.sql.SqlResult;
import com.kyouseipro.neo.dto.sql.SqlService;

public class CompanySqlBuilder {

    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  company_id INT, category NVARCHAR(255), name NVARCHAR(255), name_kana NVARCHAR(255), " +
            "  tel_number NVARCHAR(255), fax_number NVARCHAR(255), postal_code NVARCHAR(255), " +
            "  full_address NVARCHAR(255), email NVARCHAR(255), web_address NVARCHAR(255), is_original_price INT, " +
            "  version INT, state INT" +
            "); ";
    }

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.company_id, INSERTED.category, INSERTED.name, INSERTED.name_kana, " +
            "INSERTED.tel_number, INSERTED.fax_number, INSERTED.postal_code, INSERTED.full_address, " +
            "INSERTED.email, INSERTED.web_address, INSERTED.is_original_price, INSERTED.version, INSERTED.state ";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
        return
            "INSERT INTO companies_log (" +
            "  company_id, editor, process, log_date, category, name, name_kana, tel_number, fax_number, " +
            "  postal_code, full_address, email, web_address, is_original_price, version, state" +
            ") " +
            "SELECT company_id, ?, '" + processName + "', CURRENT_TIMESTAMP, category, name, name_kana, tel_number, fax_number, " +
            "  postal_code, full_address, email, web_address, is_original_price, version, state " +
            "FROM " + rowTableName + ";";
    }

    public static SqlResult buildInsert(String table, Map<String, Object> req) {

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();

        for (Map.Entry<String, Object> entry : req.entrySet()) {

            String key = entry.getKey();

            String column = SqlService.toSnake(key);

            columns.add(column);
            values.add("?");
            params.add(entry.getValue()); // nullもOK
        }

        if (columns.isEmpty()) {
            throw new IllegalArgumentException("登録項目がありません");
        }

        sql.append("INSERT INTO ").append(table).append(" (");
        sql.append(String.join(", ", columns));
        sql.append(") VALUES (");
        sql.append(String.join(", ", values));
        sql.append(")");

        return new SqlResult(sql.toString(), params);
    }

    public static SqlResult buildUpdate(String table, Map<String, Object> req, String idKey, String versionKey) {

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        List<String> sets = new ArrayList<>();

        for (Map.Entry<String, Object> entry : req.entrySet()) {

            String key = entry.getKey();

            // 除外項目
            if (key.equals(idKey) || key.equals(versionKey)) continue;

            String column = SqlService.toSnake(key);

            sets.add(column + " = ?");
            params.add(entry.getValue()); // ← nullもそのまま入る（削除対応）
        }

        // 共通
        sets.add("version = version + 1");
        sets.add("update_date = SYSDATETIME()");

        if (sets.isEmpty()) {
            throw new IllegalArgumentException("更新項目がありません");
        }

        sql.append("UPDATE ").append(table).append(" SET ");
        sql.append(String.join(", ", sets));
        sql.append(" WHERE ").append(SqlService.toSnake(idKey)).append(" = ?");
        sql.append(" AND ").append(SqlService.toSnake(versionKey)).append(" = ?");

        // WHEREパラメータ
        params.add(req.get(idKey));
        params.add(req.get(versionKey));

        return new SqlResult(sql.toString(), params);
    }

    // public static String buildBulkInsert(CompanyRequest req) {

    //     StringBuilder sql = new StringBuilder();
    //     sql.append(buildLogTable("@InsertedRows"));
    //     sql.append("INSERT INTO companies (");

    //     List<String> sets = new ArrayList<>();

    //     if (req.getCategory() != null) {
    //         sets.add("category");
    //     }

    //     if (req.getName() != null) {
    //         sets.add("name");
    //     }
    //     if (req.getNameKana() != null) {
    //         sets.add("name_kana");
    //     }

    //     if (req.getTelNumber() != null) {
    //         sets.add("tel_number");
    //     }
    //     if (req.getPostalCode() != null) {
    //         sets.add("postal_code");
    //     }
    //     if (req.getFullAddress() != null) {
    //         sets.add("full_address");
    //     }
    //     if (req.getEmail() != null) {
    //         sets.add("email");
    //     }

    //     if (req.getWebAddress() != null) {
    //         sets.add("web_address");
    //     }
    //     if (req.getIsOriginalPrice() != null) {
    //         sets.add("is_original_price");
    //     }

    //     if (sets.isEmpty()) {
    //         throw new IllegalArgumentException("更新項目がありません");
    //     }

    //     sql.append(String.join(", ", sets));
    //     sql.append(") ");

    //     sql.append(buildOutputLog() + "INTO @InsertedRows ");

    //     sql.append("VALUES (");
    //     sql.append(sets.stream().map(v -> "?").collect(Collectors.joining(",")));
    //     sql.append(");");

    //     sql.append(buildInsertLog("@InsertedRows", "INSERT"));
    //     sql.append("SELECT company_id FROM @InsertedRows;");

    //     return  sql.toString();
    // }

    // public static String buildBulkUpdate(CompanyRequest req) {

    //     StringBuilder sql = new StringBuilder();

    //     sql.append(buildLogTable("@UpdatedRows"));
    //     sql.append("UPDATE companies SET ");

    //     List<String> sets = new ArrayList<>();

    //     if (req.getCategory() != null) {
    //         sets.add("category = ?");
    //     }

    //     if (req.getName() != null) {
    //         sets.add("name = ?");
    //     }
    //     if (req.getNameKana() != null) {
    //         sets.add("name_kana = ?");
    //     }

    //     if (req.getTelNumber() != null) {
    //         sets.add("tel_number = ?");
    //     }
    //     if (req.getPostalCode() != null) {
    //         sets.add("postal_code = ?");
    //     }
    //     if (req.getFullAddress() != null) {
    //         sets.add("full_address = ?");
    //     }
    //     if (req.getEmail() != null) {
    //         sets.add("email = ?");
    //     }

    //     if (req.getWebAddress() != null) {
    //         sets.add("web_address = ?");
    //     }
    //     if (req.getIsOriginalPrice() != null) {
    //         sets.add("is_original_price = ?");
    //     }

    //     // 共通更新項目
    //     sets.add("version = version + 1");
    //     sets.add("update_date = SYSDATETIME() ");

    //     if (sets.isEmpty()) {
    //         throw new IllegalArgumentException("更新項目がありません");
    //     }

    //     sql.append(String.join(", ", sets));

    //     sql.append(buildOutputLog() + "INTO @UpdatedRows ");
    //     sql.append("WHERE company_id = ? AND version = ? AND NOT (state = ?);");
    //     sql.append(buildInsertLog("@UpdatedRows", "UPDATE"));
    //     // sql.append("SELECT company_id FROM @UpdatedRows;");

    //     return sql.toString();
    // }

    // public static String buildInsert() {
    //     return
    //         buildLogTable("@Inserted") +

    //         "INSERT INTO companies (" +
    //         "  category, name, name_kana, tel_number, fax_number, postal_code, full_address, " +
    //         "  email, web_address, is_original_price, version, state" +
    //         ") " +
    //         buildOutputLog() + "INTO @Inserted " +
    //         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); " +

    //         buildInsertLog("@Inserted", "INSERT") +
    //         "SELECT company_id FROM @Inserted;";
    // }

    // public static String buildUpdate() {
    //     return
    //         buildLogTable("@Updated") +

    //         "UPDATE companies SET " +
    //         "  category=?, name=?, name_kana=?, tel_number=?, fax_number=?, postal_code=?, " +
    //         "  full_address=?, email=?, web_address=?, is_original_price=?, version=?, state=? " +
    //         buildOutputLog() + "INTO @Updated " +
    //         "WHERE company_id=? AND version=?; " +

    //         buildInsertLog("@Updated", "UPDATE") +
    //         "SELECT company_id FROM @Updated;";
    // }

    public static String buildDeleteByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count);

        return
            buildLogTable("@Deleted") +

            "UPDATE companies SET state = ? " +
            buildOutputLog() + "INTO @Deleted " +
            "WHERE company_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLog("@Deleted", "DELETE") +
            "SELECT company_id FROM @Deleted;";
    }

    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count);
        return "SELECT * FROM companies WHERE company_id IN (" + placeholders + ") AND NOT (state = ?)";
    }
}
