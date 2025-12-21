package com.kyouseipro.neo.query.sql.management.qualification;

public class QualificationFilesSqlBuilder {

    private static String buildLogTableSql(String tableName) {
        return
            "DECLARE " + tableName + " TABLE (" +
            "  qualifications_files_id INT, qualifications_id INT, file_name NVARCHAR(255), " +
            "  internal_name NVARCHAR(255), folder_name NVARCHAR(255), version INT, state INT" +
            "); ";
    }

    private static String buildOutputLogSql() {
        return
            "OUTPUT INSERTED.qualifications_files_id, INSERTED.qualifications_id, INSERTED.file_name, " +
            "INSERTED.internal_name, INSERTED.folder_name, INSERTED.version, INSERTED.state ";
    }

    private static String buildInsertLogSql(String tableName, String process) {
        return
            "INSERT INTO qualifications_files_log (" +
            "  qualifications_files_id, editor, process, log_date, " +
            "  qualifications_id, file_name, internal_name, folder_name, version, state" +
            ") " +
            "SELECT qualifications_files_id, ?, '" + process + "', CURRENT_TIMESTAMP, " +
            "  qualifications_id, file_name, internal_name, folder_name, version, state " +
            "FROM " + tableName + "; ";
    }

    // public static String buildInsertQualificationFilesSql(int count) {
    //     return
    //         buildLogTableSql("@Inserted") +

    //         "INSERT INTO qualifications_files (" +
    //         "  qualifications_id, file_name, internal_name, folder_name, version, state" +
    //         ") " +
    //         buildOutputLogSql() + "INTO @Inserted " +
    //         "VALUES (?, ?, ?, ?, ?, ?); " +

    //         buildInsertLogSql("@Inserted", "INSERT") +
    //         "SELECT qualifications_files_id FROM @Inserted;";
    // }
    public static String buildInsertQualificationFilesSql(int count) {
        StringBuilder sqlBuilder = new StringBuilder();

        for (int i = 0; i < count; i++) {
            String insertedVar = "@Inserted" + i;
            sqlBuilder.append(buildLogTableSql(insertedVar));

            sqlBuilder.append(
                "INSERT INTO qualifications_files (" +
                "  qualifications_id, file_name, internal_name, folder_name, version, state" +
                ") " +
                buildOutputLogSql() + "INTO " + insertedVar + " " +
                "VALUES (?, ?, ?, ?, ?, ?); "
            );

            sqlBuilder.append(buildInsertLogSql(insertedVar, "INSERT"));
            sqlBuilder.append("SELECT qualifications_files_id FROM " + insertedVar + "; ");
        }

        return sqlBuilder.toString();
    }

    // public static String buildUpdateQualificationFilesSql() {
    //     return
    //         buildLogTableSql("@Updated") +

    //         "UPDATE qualifications_files SET " +
    //         "  qualifications_id=?, file_name=?, internal_name=?, folder_name=?, version=?, state=? " +
    //         buildOutputLogSql() + "INTO @Updated " +
    //         "WHERE qualifications_files_id=?; " +

    //         buildInsertLogSql("@Updated", "UPDATE") +
    //         "SELECT qualifications_files_id FROM @Updated;";
    // }

    public static String buildDeleteQualificationFilesSql() {
        return
            buildLogTableSql("@Deleted") +

            "UPDATE qualifications_files SET state=? " +
            buildOutputLogSql() + "INTO @Deleted " +
            "WHERE folder_name=?; " +

            buildInsertLogSql("@Deleted", "DELETE") +
            "SELECT qualifications_files_id FROM @Deleted;";
    }

    public static String buildFindByQualificationsFilesIdSql() {
        return "SELECT * FROM qualifications_files WHERE NOT (state = ?) AND qualifications_files_id = ?";
    }

    public static String buildFindAllByQualificationsIdSql() {
        return "SELECT * FROM qualifications_files WHERE NOT (state = ?) AND qualifications_id = ?";
    }
}

