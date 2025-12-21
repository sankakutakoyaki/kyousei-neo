package com.kyouseipro.neo.query.sql.management.recycle;

import com.kyouseipro.neo.common.Utilities;

public class RecycleMakerSqlBuilder {
    public static String buildInsertRecycleMakerSql() {
        return
            "INSERT INTO recycle_makers (code, name, [group], version, state) OUTPUT INSERTED.recycle_maker_id VALUES (?, ?, ?, ?, ?);";
    }

    public static String buildUpdateRecycleMakerSql() {
        return
            "UPDATE recycle_makers SET code=?, name=?, [group]=?, version=?, state=? WHERE recycle_maker_id=?;";
    }

    public static String buildDeleteRecycleMakerSql() {
        return
            "UPDATE recycle_makers SET state = ? OUTPUT INSERTED.recycle_maker_id WHERE recycle_maker_id = ?;";
    }

    public static String buildDeleteRecycleMakerForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"
        return
            "UPDATE recycle_makers SET state = ? WHERE recycle_maker_id IN (" + placeholders + ") AND NOT (state = ?);";
    }

    private static String baseSelectString() {
        return
            "SELECT rm.recycle_maker_id, rm.code, rm.name, [rm].[group], rm.version, rm.state FROM recycle_makers rm";
    }

    public static String buildFindByIdSql() {
        return 
            baseSelectString() + " WHERE rm.recycle_maker_id = ? AND NOT (rm.state = ?)";
    }

    public static String buildFindByCodeSql() {
        return 
            baseSelectString() + " WHERE rm.code = ? AND NOT (rm.state = ?)";
    }

    public static String buildDownloadCsvRecycleMakerForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count);
        return 
            baseSelectString() + " WHERE rm.recycle_maker_id IN (" + placeholders + ") AND NOT (rm.state = ?)";
    }
}