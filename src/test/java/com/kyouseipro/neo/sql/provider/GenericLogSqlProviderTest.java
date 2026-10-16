package com.kyouseipro.neo.sql.provider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kyouseipro.neo.sql.service.SqlTableMetadataService;
import com.kyouseipro.neo.sql.service.SqlTableMetadataService.ColumnMeta;

class GenericLogSqlProviderTest {

    private GenericLogSqlProvider provider;

    @BeforeEach
    void setUp() {
        SqlTableMetadataService metadata = mock(SqlTableMetadataService.class);

        when(metadata.getColumns("companies")).thenReturn(List.of(
            new ColumnMeta("company_id", "int", 4, 10, 0, false, true, false),
            new ColumnMeta("name", "nvarchar", 510, 0, 0, true, false, false),
            new ColumnMeta("price", "decimal", 9, 12, 2, true, false, false),
            new ColumnMeta("regist_date", "datetime2", 8, 0, 7, false, false, false),
            new ColumnMeta("update_date", "datetime2", 8, 0, 7, false, false, false),
            new ColumnMeta("version", "int", 4, 10, 0, false, false, false),
            new ColumnMeta("state", "int", 4, 10, 0, false, false, false)
        ));

        doNothing().when(metadata).validateLogTable("companies");
        provider = new GenericLogSqlProvider("companies", metadata);
    }

    @Test
    void generatesTableVariable() {
        String sql = provider.buildLogTable("@InsertedRows");

        assertTrue(sql.contains("[company_id] INT NULL"));
        assertTrue(sql.contains("[name] NVARCHAR(255) NULL"));
        assertTrue(sql.contains("[price] DECIMAL(12,2) NULL"));
        assertTrue(sql.contains("[regist_date] DATETIME2(7) NULL"));
        assertTrue(sql.contains("[update_date] DATETIME2(7) NULL"));
    }

    @Test
    void generatesOutputForAllColumns() {
        String sql = provider.buildOutput();

        assertTrue(sql.contains("INSERTED.[company_id]"));
        assertTrue(sql.contains("INSERTED.[name]"));
        assertTrue(sql.contains("INSERTED.[regist_date]"));
        assertTrue(sql.contains("INSERTED.[update_date]"));
        assertTrue(sql.contains("INSERTED.[version]"));
        assertTrue(sql.contains("INSERTED.[state]"));
    }

    @Test
    void generatesInsertLog() {
        String sql = provider.buildInsertLog("@InsertedRows", "INSERT");

        assertTrue(sql.contains("INSERT INTO [dbo].[companies_log]"));
        assertTrue(sql.contains("[editor], [process], [log_date]"));
        assertTrue(sql.contains("[regist_date]"));
        assertTrue(sql.contains("[update_date]"));
        assertTrue(sql.contains("FROM @InsertedRows"));
    }

    @Test
    void generatesLogParams() {
        assertEquals(
            List.of("test@example.com", "UPDATE"),
            provider.buildLogParams(
                Map.of("editor", "test@example.com"),
                "UPDATE"
            )
        );
    }

    @Test
    void rejectsMissingEditor() {
        assertThrows(IllegalArgumentException.class,
            () -> provider.buildLogParams(Map.of(), "INSERT"));
    }

    @Test
    void validatesLogTable() {
        SqlTableMetadataService metadata = mock(SqlTableMetadataService.class);
        when(metadata.getColumns("companies")).thenReturn(List.of(
            new ColumnMeta("company_id", "int", 4, 10, 0, false, true, false)
        ));

        new GenericLogSqlProvider("companies", metadata);
        verify(metadata).validateLogTable("companies");
    }

    @Test
    void rejectsUnsupportedDataType() {
        SqlTableMetadataService metadata = mock(SqlTableMetadataService.class);

        when(metadata.getColumns("test_table")).thenReturn(List.of(
            new ColumnMeta("test_id", "int", 4, 10, 0, false, true, false),
            new ColumnMeta("data", "xml", -1, 0, 0, true, false, false)
        ));

        GenericLogSqlProvider provider =
            new GenericLogSqlProvider("test_table", metadata);

        assertThrows(IllegalStateException.class,
            () -> provider.buildLogTable("@InsertedRows"));
    }
}