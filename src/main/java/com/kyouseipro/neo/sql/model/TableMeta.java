package com.kyouseipro.neo.sql.model;

public class TableMeta {

    private final String tableName;
    private final String idColumn;
    private final String stateColumn;
    private final String versionColumn;

    public TableMeta(
            String tableName,
            String idColumn,
            String stateColumn,
            String versionColumn
    ) {
        this.tableName = tableName;
        this.idColumn = idColumn;
        this.stateColumn = stateColumn;
        this.versionColumn = versionColumn;
    }

    public String tableName() { return tableName; }
    public String idColumn() { return idColumn; }
    public String stateColumn() { return stateColumn; }
    public String versionColumn() { return versionColumn;}
}