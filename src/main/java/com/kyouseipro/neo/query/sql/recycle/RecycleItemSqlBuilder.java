package com.kyouseipro.neo.query.sql.recycle;

import com.kyouseipro.neo.common.Utilities;

public class RecycleItemSqlBuilder {
    public static String buildInsertRecycleItemSql() {
        return
            "INSERT INTO recycle_items (code, name, version, state) OUTPUT INSERTED.recycle_item_id VALUES (?, ?, ?, ?);";
    }

    public static String buildUpdateRecycleItemSql() {
        return
            "UPDATE recycle_items SET code=?, name=?, version=?, state=? WHERE recycle_item_id=?;";
    }

    public static String buildDeleteRecycleItemSql() {
        return
            "UPDATE recycle_items SET state = ? OUTPUT INSERTED.recycle_item_id WHERE recycle_item_id = ?;";
    }

    public static String buildDeleteRecycleItemForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count); // "?, ?, ..., ?"
        return
            "UPDATE recycle_items SET state = ? WHERE recycle_item_id IN (" + placeholders + ") AND NOT (state = ?);";
    }

    private static String baseSelectString() {
        return
            "SELECT ri.recycle_item_id, ri.code, ri.name, ri,version, ri.state FROM recycle_item ri";
    }

    public static String buildFindByIdSql() {
        return 
            baseSelectString() + " WHERE ri.recycle_item_id = ? AND NOT (ri.state = ?)";
    }
    
    public static String buildDownloadCsvRecycleItemForIdsSql(int count) {
        String placeholders = Utilities.generatePlaceholders(count);
        return 
            baseSelectString() + " WHERE ri.recycle_item_id IN (" + placeholders + ") AND NOT (ri.state = ?)";
    }
}