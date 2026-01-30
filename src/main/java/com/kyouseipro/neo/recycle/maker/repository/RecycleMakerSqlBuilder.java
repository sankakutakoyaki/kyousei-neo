package com.kyouseipro.neo.recycle.maker.repository;

import com.kyouseipro.neo.common.Utilities;

public class RecycleMakerSqlBuilder {
    public static String buildInsert() {
        return
            "INSERT INTO recycle_makers (code, name, abbr_name, [group], version, state) OUTPUT INSERTED.recycle_maker_id VALUES (?, ?, ?, ?, ?, ?);";
    }

    public static String buildUpdate() {
        return
            "UPDATE recycle_makers SET code=?, name=?, abbr_name=?, [group]=?, version=?, state=? WHERE recycle_maker_id=? AND version=?;";
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
            "SELECT rm.recycle_maker_id, rm.code, rm.name, rm.abbr_name, [rm].[group], rg.name as group_name, rm.version, rm.state FROM recycle_makers rm" +
            " LEFT OUTER JOIN recycle_groups rg ON rg.recycle_group_id = [rm].[group] AND NOT (rg.state = ?)";
    }

    public static String buildFindById() {
        return 
            baseSelectString() + " WHERE rm.recycle_maker_id = ? AND NOT (rm.state = ?)";
    }

    public static String buildFindByCode() {
        return 
            baseSelectString() + " WHERE rm.code = ? AND NOT (rm.state = ?)";
    }

    public static String buildFindAll() {
        return
            baseSelectString() + " WHERE NOT (rm.state = ?)";
    }

    public static String buildDownloadCsvByIds(int count) {
        String placeholders = Utilities.generatePlaceholders(count);
        return 
            baseSelectString() + " WHERE rm.recycle_maker_id IN (" + placeholders + ") AND NOT (rm.state = ?)";
    }
}