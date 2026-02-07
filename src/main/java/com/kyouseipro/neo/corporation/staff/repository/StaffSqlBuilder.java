package com.kyouseipro.neo.corporation.staff.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.corporation.staff.entity.StaffEntityRequest;

public class StaffSqlBuilder {

    private static String buildLogTable(String rowTableName) {
        return
            "DECLARE " + rowTableName + " TABLE (" +
            "  staff_id INT, company_id INT, office_id INT, " +
            "  name NVARCHAR(255), name_kana NVARCHAR(255), phone_number NVARCHAR(255), email NVARCHAR(255), " +
            "  version INT, state INT" +
            "); ";
    }

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.staff_id, INSERTED.company_id, INSERTED.office_id, " +
            "INSERTED.name, INSERTED.name_kana, INSERTED.phone_number, INSERTED.email, INSERTED.version, INSERTED.state ";
    }

    private static String buildInsertLog(String rowTableName, String processName) {
        return
            "INSERT INTO staffs_log (" +
            "  staff_id, editor, process, log_date, company_id, office_id, " +
            "  name, name_kana, phone_number, email, version, state" +
            ") " +
            "SELECT staff_id, ?, '" + processName + "', CURRENT_TIMESTAMP, " +
            "  company_id, office_id, name, name_kana, phone_number, email, version, state " +
            "FROM " + rowTableName + ";";
    }

    public static String buildBulkInsert(StaffEntityRequest req) {

        StringBuilder sql = new StringBuilder();
        sql.append(buildLogTable("@InsertedRows"));
        sql.append("INSERT INTO staffs (");

        List<String> sets = new ArrayList<>();

        if (req.getCompanyId() != null) {
            sets.add("company_id");
        }
        if (req.getOfficeId() != null) {
            sets.add("office_id");
        }

        if (req.getName() != null) {
            sets.add("name");
        }
        if (req.getNameKana() != null) {
            sets.add("name_kana");
        }

        if (req.getPhoneNumber() != null) {
            sets.add("phone_number");
        }
        if (req.getEmail() != null) {
            sets.add("email");
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
        sql.append("SELECT staff_id FROM @InsertedRows;");

        return  sql.toString();
    }

    public static String buildBulkUpdate(StaffEntityRequest req) {

        StringBuilder sql = new StringBuilder();

        sql.append(buildLogTable("@UpdatedRows"));
        sql.append("UPDATE staffs SET ");

        List<String> sets = new ArrayList<>();

        if (req.getCompanyId() != null) {
            sets.add("company_id = ?");
        }
        if (req.getOfficeId() != null) {
            sets.add("office_id = ?");
        }

        if (req.getName() != null) {
            sets.add("name = ?");
        }
        if (req.getNameKana() != null) {
            sets.add("name_kana = ?");
        }

        if (req.getPhoneNumber() != null) {
            sets.add("phone_number = ?");
        }
        if (req.getEmail() != null) {
            sets.add("email = ?");
        }

        // 共通更新項目
        sets.add("version = version + 1");
        sets.add("update_date = SYSDATETIME() ");

        if (sets.isEmpty()) {
            throw new IllegalArgumentException("更新項目がありません");
        }

        sql.append(String.join(", ", sets));

        sql.append(buildOutputLog() + "INTO @UpdatedRows ");
        sql.append("WHERE staff_id = ? AND version = ? AND NOT (state = ?);");
        sql.append(buildInsertLog("@UpdatedRows", "UPDATE"));
        sql.append("SELECT staff_id FROM @UpdatedRows;");

        return sql.toString();
    }

    // public static String buildInsert() {
    //     return
    //         buildLogTable("@Inserted") +

    //         "INSERT INTO staffs (" +
    //         "  company_id, office_id, name, name_kana, phone_number, email, version, state" +
    //         ") " +
    //         buildOutputLog() + "INTO @Inserted " +
    //         "VALUES (?, ?, ?, ?, ?, ?, ?, ?); " +

    //         buildInsertLog("@Inserted", "INSERT") +
    //         "SELECT staff_id FROM @Inserted;";
    // }

    // public static String buildUpdate() {
    //     return
    //         buildLogTable("@Updated") +

    //         "UPDATE staffs SET " +
    //         "  company_id=?, office_id=?, name=?, name_kana=?, phone_number=?, email=?, version=?, state=? " +
    //         buildOutputLog() + "INTO @Updated " +
    //         "WHERE staff_id=? AND version=?; " +

    //         buildInsertLog("@Updated", "UPDATE") +
    //         "SELECT staff_id FROM @Updated;";
    // }

    public static String buildDeleteByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count);

        return
            buildLogTable("@Deleted") +

            "UPDATE staffs SET state = ? " +
            buildOutputLog() + "INTO @Deleted " +
            "WHERE staff_id IN (" + placeholders + ") AND NOT (state = ?); " +

            buildInsertLog("@Deleted", "DELETE") +
            "SELECT staff_id FROM @Deleted;";
    }

    private static String baseSelectString() {
        return
            "SELECT s.*, c.name as company_name, o.name as office_name FROM staffs s" + 
            " INNER JOIN companies c ON c.company_id = s.company_id AND NOT (c.state = ?)" + 
            " LEFT OUTER JOIN offices o ON o.office_id = s.office_id AND NOT (o.state = ?)";
    }

    public static String buildFindById() {
        return
            baseSelectString() +
            " WHERE NOT (s.state = ?) AND s.staff_id = ?";
    }

    public static String buildFindAll() {
        return 
            baseSelectString() +
            " WHERE NOT (c.category = 0) AND NOT (s.state = ?)";
    }

    public static String buildFindByCategoryId() {
        return 
            baseSelectString() +
            " WHERE c.category = ? AND NOT (s.state = ?)";
    }

    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ?, ..."
        return
            baseSelectString() +
            " WHERE s.staff_id IN (" + placeholders + ") AND NOT (s.state = ?)";
    }
}
