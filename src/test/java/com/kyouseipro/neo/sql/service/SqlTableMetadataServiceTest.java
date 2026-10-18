package com.kyouseipro.neo.sql.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kyouseipro.neo.sql.model.SelectRequest;

class SqlTableMetadataServiceTest {

    private QueryExecutor queryExecutor;
    private SqlTableMetadataService service;

    @BeforeEach
    void setUp() {
        queryExecutor = mock(QueryExecutor.class);
        service = new SqlTableMetadataService(queryExecutor);
    }

    private Map<String, Object> column(String name, String type, boolean identity) {
        return Map.of(
            "columnName", name,
            "dataType", type,
            "maxLength", type.equals("nvarchar") ? 510 : 4,
            "numericPrecision", 10,
            "numericScale", 0,
            "isNullable", false,
            "isIdentity", identity,
            "isComputed", false
        );
    }

    @Test
    void loadsColumns() {
        when(queryExecutor.select(any(), any())).thenReturn(List.of(
            column("company_id", "int", true),
            column("name", "nvarchar", false)
        ));

        var columns = service.getColumns("companies");

        assertEquals(2, columns.size());
        assertEquals("company_id", columns.get(0).name());
        assertTrue(columns.get(0).identity());
        assertEquals("nvarchar", columns.get(1).dataType());
        assertEquals(510, columns.get(1).maxLength());
    }

    @Test
    void cachesColumns() {
        when(queryExecutor.select(any(), any())).thenReturn(List.of(
            column("company_id", "int", true)
        ));

        service.getColumns("companies");
        service.getColumns("companies");

        verify(queryExecutor, times(1)).select(any(), any());
    }

    @Test
    void rejectsInvalidTableName() {
        assertThrows(IllegalArgumentException.class,
            () -> service.getColumns("companies;DROP TABLE companies"));

        verifyNoInteractions(queryExecutor);
    }

    @Test
    void rejectsUnknownTable() {
        when(queryExecutor.select(any(), any())).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class,
            () -> service.getColumns("unknown_table"));
    }

    @Test
    void rejectsMissingLogColumn() {
        when(queryExecutor.select(any(), any())).thenAnswer(invocation -> {
            SelectRequest req = invocation.getArgument(1);
            String tableName = (String) req.getParams().get("tableName");

            if ("companies".equals(tableName)) {
                return List.of(column("company_id", "int", true));
            }
            return List.of(column("other_column", "int", false));
        });

        assertThrows(IllegalStateException.class,
            () -> service.validateLogTable("companies"));
    }

    @Test
    void validatesLogColumns() {
        when(queryExecutor.select(any(), any())).thenAnswer(invocation -> {
            SelectRequest req = invocation.getArgument(1);
            String tableName = (String) req.getParams().get("tableName");

            if ("companies".equals(tableName)) {
                return List.of(column("company_id", "int", true));
            }

            return List.of(
                column("company_id", "int", false),
                logColumn("editor", "nvarchar", 510),
                logColumn("process", "nvarchar", 100),
                logColumn("log_date", "datetime2", 8)
            );
        });

        assertDoesNotThrow(() -> service.validateLogTable("companies"));
    }

    @Test
    void rejectsMissingEditorColumn() {
        when(queryExecutor.select(any(), any())).thenAnswer(invocation -> {
            SelectRequest req = invocation.getArgument(1);
            String tableName = (String) req.getParams().get("tableName");

            if ("companies".equals(tableName)) {
                return List.of(column("company_id", "int", true));
            }

            return List.of(
                column("company_id", "int", false),
                logColumn("process", "nvarchar", 100),
                logColumn("log_date", "datetime2", 8)
            );
        });

        assertThrows(IllegalStateException.class,
            () -> service.validateLogTable("companies"));
    }

    @Test
    void rejectsShortEditorColumn() {
        when(queryExecutor.select(any(), any())).thenAnswer(invocation -> {
            SelectRequest req = invocation.getArgument(1);
            String tableName = (String) req.getParams().get("tableName");

            if ("companies".equals(tableName)) {
                return List.of(column("company_id", "int", true));
            }

            return List.of(
                column("company_id", "int", false),
                logColumn("editor", "nvarchar", 100),
                logColumn("process", "nvarchar", 100),
                logColumn("log_date", "datetime2", 8)
            );
        });

        assertThrows(IllegalStateException.class,
            () -> service.validateLogTable("companies"));
    }

    @Test
    void hasLogTableReturnsTrue() {
        when(queryExecutor.select(any(), any()))
            .thenReturn(List.of(Map.of("tableCount", 1)));

        assertTrue(service.hasLogTable("companies"));

        verify(queryExecutor).select(any(), any());
    }

    @Test
    void hasLogTableReturnsFalse() {
        when(queryExecutor.select(any(), any()))
            .thenReturn(List.of(Map.of("tableCount", 0)));

        assertFalse(service.hasLogTable("unknown"));
    }

    @Test
    void hasLogTableUsesCache() {
        when(queryExecutor.select(any(), any()))
            .thenReturn(List.of(Map.of("tableCount", 1)));

        assertTrue(service.hasLogTable("companies"));
        assertTrue(service.hasLogTable("companies"));

        verify(queryExecutor, times(1)).select(any(), any());
    }

    @Test
    void hasLogTableRejectsInvalidName() {
        assertThrows(IllegalArgumentException.class,
            () -> service.hasLogTable("companies;DROP TABLE companies"));

        verifyNoInteractions(queryExecutor);
    }

    @Test
    void clearLogSchemaCacheRemovesCachedColumns() {
        when(queryExecutor.select(any(), any())).thenReturn(List.of(
            Map.of(
                "columnName", "employee_id",
                "dataType", "bigint",
                "maxLength", 8,
                "numericPrecision", 19,
                "numericScale", 0,
                "isNullable", false,
                "isIdentity", true,
                "isComputed", false
            )
        ));

        service.getColumns("employees");
        service.getColumns("employees");

        verify(queryExecutor, times(1)).select(any(), any());

        service.clearLogSchemaCache("employees");
        service.getColumns("employees");

        verify(queryExecutor, times(2)).select(any(), any());
    }

    private Map<String, Object> logColumn(String name, String type, int maxLength) {
        return Map.of(
            "columnName", name,
            "dataType", type,
            "maxLength", maxLength,
            "numericPrecision", 0,
            "numericScale", type.equals("datetime2") ? 7 : 0,
            "isNullable", true,
            "isIdentity", false,
            "isComputed", false
        );
    }
}