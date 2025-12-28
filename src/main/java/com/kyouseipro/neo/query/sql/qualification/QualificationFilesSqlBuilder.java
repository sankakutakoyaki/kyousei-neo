package com.kyouseipro.neo.query.sql.qualification;

public class QualificationFilesSqlBuilder {

    private static String buildLogTable(String tableName) {
        return
            "DECLARE " + tableName + " TABLE (" +
            "  qualifications_files_id INT, qualifications_id INT, file_name NVARCHAR(255), " +
            "  internal_name NVARCHAR(255), folder_name NVARCHAR(255), version INT, state INT" +
            "); ";
    }

    private static String buildOutputLog() {
        return
            "OUTPUT INSERTED.qualifications_files_id, INSERTED.qualifications_id, INSERTED.file_name, " +
            "INSERTED.internal_name, INSERTED.folder_name, INSERTED.version, INSERTED.state ";
    }

    private static String buildInsertLog(String tableName, String process) {
        return
            "INSERT INTO qualifications_files_log (" +
            "  qualifications_files_id, editor, process, log_date, " +
            "  qualifications_id, file_name, internal_name, folder_name, version, state" +
            ") " +
            "SELECT qualifications_files_id, ?, '" + process + "', CURRENT_TIMESTAMP, " +
            "  qualifications_id, file_name, internal_name, folder_name, version, state " +
            "FROM " + tableName + "; ";
    }

    public static String buildInsert(int count) {
        StringBuilder sqlBuilder = new StringBuilder();

        for (int i = 0; i < count; i++) {
            String insertedVar = "@Inserted" + i;
            sqlBuilder.append(buildLogTable(insertedVar));

            sqlBuilder.append(
                "INSERT INTO qualifications_files (" +
                "  qualifications_id, file_name, internal_name, folder_name, version, state" +
                ") " +
                buildOutputLog() + "INTO " + insertedVar + " " +
                "VALUES (?, ?, ?, ?, ?, ?); "
            );

            sqlBuilder.append(buildInsertLog(insertedVar, "INSERT"));
            sqlBuilder.append("SELECT qualifications_files_id FROM " + insertedVar + "; ");
        }

        return sqlBuilder.toString();
    }

    public static String buildDelete() {
        return
            buildLogTable("@Deleted") +

            "UPDATE qualifications_files SET state=? " +
            buildOutputLog() + "INTO @Deleted " +
            "WHERE folder_name=?; " +

            buildInsertLog("@Deleted", "DELETE") +
            "SELECT qualifications_files_id FROM @Deleted;";
    }

    public static String buildFindById() {
        return "SELECT * FROM qualifications_files WHERE NOT (state = ?) AND qualifications_files_id = ?";
    }

    public static String buildFindAllByQualificationsId() {
        return "SELECT * FROM qualifications_files WHERE NOT (state = ?) AND qualifications_id = ?";
    }
}

