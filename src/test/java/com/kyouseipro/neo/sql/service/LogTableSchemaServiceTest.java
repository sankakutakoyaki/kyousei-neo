package com.kyouseipro.neo.sql.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kyouseipro.neo.sql.service.SqlTableMetadataService.ColumnMeta;

class LogTableSchemaServiceTest {

    private SqlTableMetadataService metadata;
    private LogTableSchemaService service;

    @BeforeEach
    void setUp() {
        metadata = mock(SqlTableMetadataService.class);
        service = new LogTableSchemaService(metadata);
    }

    @Test
    void createsLogTableSql() {
        when(metadata.getColumns("expenses")).thenReturn(List.of(
            col("expense_id", "bigint", 8, 19, 0, false, true, false),
            col("amount", "decimal", 9, 12, 2, false, false, false),
            col("remarks", "nvarchar", 1000, 0, 0, true, false, false)
        ));

        String sql = service.createLogTableSql("expenses");

        assertTrue(sql.contains("CREATE TABLE [dbo].[expenses_log]"));
        assertTrue(sql.contains("[editor] NVARCHAR(255) NOT NULL"));
        assertTrue(sql.contains("[process] NVARCHAR(50) NOT NULL"));
        assertTrue(sql.contains("[log_date] DATETIME2(7) NOT NULL"));
        assertTrue(sql.contains("[expense_id] BIGINT NOT NULL"));
        assertTrue(sql.contains("[amount] DECIMAL(12,2) NOT NULL"));
        assertTrue(sql.contains("[remarks] NVARCHAR(500) NULL"));

        assertFalse(sql.contains("IDENTITY"));
        assertFalse(sql.contains("PRIMARY KEY"));
    }

    @Test
    void supportsMaxLength() {
        when(metadata.getColumns("expenses")).thenReturn(List.of(
            col("remarks", "nvarchar", -1, 0, 0, true, false, false)
        ));

        String sql = service.createLogTableSql("expenses");

        assertTrue(sql.contains("[remarks] NVARCHAR(MAX) NULL"));
    }

    @Test
    void convertsComputedColumnToNormalColumn() {
        when(metadata.getColumns("expenses")).thenReturn(List.of(
            col("total", "decimal", 9, 12, 2, true, false, true)
        ));

        String sql = service.createLogTableSql("expenses");

        assertTrue(sql.contains("[total] DECIMAL(12,2) NULL"));
        assertFalse(sql.contains(" AS "));
    }

    @Test
    void rejectsUnsupportedDataType() {
        when(metadata.getColumns("expenses")).thenReturn(List.of(
            col("data", "xml", -1, 0, 0, true, false, false)
        ));

        assertThrows(IllegalStateException.class,
            () -> service.createLogTableSql("expenses"));
    }

    @Test
    void rejectsReservedLogColumnName() {
        when(metadata.getColumns("expenses")).thenReturn(List.of(
            col("editor", "nvarchar", 200, 0, 0, true, false, false)
        ));

        assertThrows(IllegalStateException.class,
            () -> service.createLogTableSql("expenses"));
    }

    @Test
    void rejectsLogTableAsSource() {
        assertThrows(IllegalArgumentException.class,
            () -> service.createLogTableSql("expenses_log"));
    }

    private ColumnMeta col(
            String name, String type, int length,
            int precision, int scale,
            boolean nullable, boolean identity, boolean computed) {

        return new ColumnMeta(
            name, type, length, precision, scale,
            nullable, identity, computed
        );
    }

    @Test
    void generatesAlterSqlForMissingColumn() {
        when(metadata.getColumns("employees")).thenReturn(List.of(
            col("employee_id", "bigint", 8, 19, 0, false, true, false),
            col("emergency_phone", "nvarchar", 40, 0, 0, false, false, false)
        ));
        when(metadata.getColumns("employees_log")).thenReturn(List.of(
            col("employee_id", "bigint", 8, 19, 0, false, false, false)
        ));

        String sql = service.addMissingColumnsSql("employees");

        assertEquals(
            "ALTER TABLE [dbo].[employees_log] ADD [emergency_phone] NVARCHAR(20) NULL;",
            sql
        );
    }

    @Test
    void returnsEmptyWhenAllColumnsExist() {
        var columns = List.of(
            col("employee_id", "bigint", 8, 19, 0, false, true, false)
        );

        when(metadata.getColumns("employees")).thenReturn(columns);
        when(metadata.getColumns("employees_log")).thenReturn(columns);

        assertEquals("", service.addMissingColumnsSql("employees"));
    }

    @Test
    void detectsColumnTypeMismatch() {
        when(metadata.getColumns("employees")).thenReturn(List.of(
            col("employee_id", "bigint", 8, 19, 0, false, true, false),
            col("salary", "decimal", 9, 12, 2, false, false, false)
        ));
        when(metadata.getColumns("employees_log")).thenReturn(List.of(
            col("employee_id", "bigint", 8, 19, 0, false, false, false),
            col("salary", "decimal", 9, 10, 2, false, false, false)
        ));

        assertEquals(List.of("salary"), service.findColumnMismatches("employees"));
    }

    @Test
    void returnsEmptyWhenColumnsMatch() {
        var columns = List.of(
            col("employee_id", "bigint", 8, 19, 0, false, true, false)
        );

        when(metadata.getColumns("employees")).thenReturn(columns);
        when(metadata.getColumns("employees_log")).thenReturn(columns);

        assertTrue(service.findColumnMismatches("employees").isEmpty());
    }

    @Test
    void diagnosesMissingLogTable() {
        when(metadata.hasLogTable("expenses")).thenReturn(false);
        when(metadata.getColumns("expenses")).thenReturn(List.of(
            col("expense_id", "bigint", 8, 19, 0, false, true, false)
        ));

        var result = service.diagnose("expenses");

        assertFalse(result.logTableExists());
        assertTrue(result.createSql().contains("CREATE TABLE [dbo].[expenses_log]"));
        assertEquals("", result.alterSql());
        assertTrue(result.mismatches().isEmpty());

        verify(metadata).clearLogSchemaCache("expenses");
    }

    @Test
    void diagnosesMissingColumn() {
        when(metadata.hasLogTable("expenses")).thenReturn(true);
        when(metadata.getColumns("expenses")).thenReturn(List.of(
            col("expense_id", "bigint", 8, 19, 0, false, true, false),
            col("remarks", "nvarchar", 1000, 0, 0, true, false, false)
        ));
        when(metadata.getColumns("expenses_log")).thenReturn(List.of(
            col("expense_id", "bigint", 8, 19, 0, false, false, false),
            col("editor", "nvarchar", 510, 0, 0, false, false, false),
            col("process", "nvarchar", 100, 0, 0, false, false, false),
            col("log_date", "datetime2", 8, 27, 7, false, false, false)
        ));

        var result = service.diagnose("expenses");

        assertTrue(result.logTableExists());
        assertEquals("", result.createSql());
        assertTrue(result.alterSql().contains(
            "ALTER TABLE [dbo].[expenses_log] ADD [remarks] NVARCHAR(500) NULL;"
        ));
        assertTrue(result.mismatches().isEmpty());
    }

    @Test
    void diagnosesColumnMismatch() {
        when(metadata.hasLogTable("expenses")).thenReturn(true);
        when(metadata.getColumns("expenses")).thenReturn(List.of(
            col("amount", "decimal", 9, 12, 2, false, false, false)
        ));
        when(metadata.getColumns("expenses_log")).thenReturn(List.of(
            col("amount", "decimal", 9, 10, 2, false, false, false),
            col("editor", "nvarchar", 510, 0, 0, false, false, false),
            col("process", "nvarchar", 100, 0, 0, false, false, false),
            col("log_date", "datetime2", 8, 27, 7, false, false, false)
        ));

        var result = service.diagnose("expenses");

        assertTrue(result.logTableExists());
        assertEquals("", result.alterSql());
        assertEquals(List.of("amount"), result.mismatches());
    }

    @Test
    void diagnosesMissingLogColumn() {
        when(metadata.hasLogTable("expenses")).thenReturn(true);
        when(metadata.getColumns("expenses")).thenReturn(List.of(
            col("expense_id", "bigint", 8, 19, 0, false, true, false)
        ));
        when(metadata.getColumns("expenses_log")).thenReturn(List.of(
            col("expense_id", "bigint", 8, 19, 0, false, false, false),
            col("editor", "nvarchar", 510, 0, 0, false, false, false),
            col("log_date", "datetime2", 8, 27, 7, false, false, false)
        ));

        var result = service.diagnose("expenses");

        assertTrue(result.mismatches().contains("process: カラムがありません"));
    }
}