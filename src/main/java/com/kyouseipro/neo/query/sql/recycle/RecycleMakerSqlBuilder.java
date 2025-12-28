package com.kyouseipro.neo.query.sql.recycle;

import com.kyouseipro.neo.common.Utilities;

public class RecycleMakerSqlBuilder {
    public static String buildInsert() {
        return
            "INSERT INTO recycle_makers (code, name, [group], version, state) OUTPUT INSERTED.recycle_maker_id VALUES (?, ?, ?, ?, ?);";
    }

    public static String buildUpdate() {
        return
            "UPDATE recycle_makers SET code=?, name=?, [group]=?, version=?, state=? WHERE recycle_maker_id=? AND version=?;";
    }

    public static String buildDelete() {
        return
            "UPDATE recycle_makers SET state = ? OUTPUT INSERTED.recycle_maker_id WHERE recycle_maker_id = ?;";
    }

    public static String buildDeleteByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"
        return
            "UPDATE recycle_makers SET state = ? WHERE recycle_maker_id IN (" + placeholders + ") AND NOT (state = ?);";
    }

    private static String baseSelectString() {
        return
            "SELECT rm.recycle_maker_id, rm.code, rm.name, [rm].[group], rm.version, rm.state FROM recycle_makers rm";
    }

    public static String buildFindById() {
        return 
            baseSelectString() + " WHERE rm.recycle_maker_id = ? AND NOT (rm.state = ?)";
    }

    public static String buildFindByCode() {
        return 
            baseSelectString() + " WHERE rm.code = ? AND NOT (rm.state = ?)";
    }

    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count);
        return 
            baseSelectString() + " WHERE rm.recycle_maker_id IN (" + placeholders + ") AND NOT (rm.state = ?)";
    }
}