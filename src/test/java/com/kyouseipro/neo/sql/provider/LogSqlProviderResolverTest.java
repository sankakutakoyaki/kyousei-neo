package com.kyouseipro.neo.sql.provider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kyouseipro.neo.interfaces.sql.LogSqlProvider;
import com.kyouseipro.neo.sql.service.SqlTableMetadataService;
import com.kyouseipro.neo.sql.service.SqlTableMetadataService.ColumnMeta;

class LogSqlProviderResolverTest {

    private SqlTableMetadataService metadataService;
    private LogSqlProviderResolver resolver;

    @BeforeEach
    void setUp() {
        metadataService = mock(SqlTableMetadataService.class);
        resolver = new LogSqlProviderResolver(metadataService);
    }

    @Test
    void usesGenericProviderWhenLogTableExists() {
        when(metadataService.hasLogTable("companies")).thenReturn(true);
        when(metadataService.getColumns("companies")).thenReturn(columns());

        LogSqlProvider provider = resolver.resolve("companies");

        assertInstanceOf(GenericLogSqlProvider.class, provider);
        verify(metadataService).validateLogTable("companies");
    }

    @Test
    void cachesGenericProvider() {
        when(metadataService.hasLogTable("companies")).thenReturn(true);
        when(metadataService.getColumns("companies")).thenReturn(columns());

        LogSqlProvider first = resolver.resolve("companies");
        LogSqlProvider second = resolver.resolve("companies");

        assertSame(first, second);
        verify(metadataService, times(1)).validateLogTable("companies");
    }

    @Test
    void returnsNullWhenLogTableDoesNotExist() {
        when(metadataService.hasLogTable("companies")).thenReturn(false);

        assertNull(resolver.resolve("companies"));
        verify(metadataService, never()).validateLogTable(anyString());
    }

    @Test
    void returnsNullWhenNoProviderExists() {
        when(metadataService.hasLogTable("unknown")).thenReturn(false);

        assertNull(resolver.resolve("unknown"));
    }

    @Test
    void rejectsInvalidLogTableStructure() {
        when(metadataService.hasLogTable("companies")).thenReturn(true);
        doThrow(new IllegalStateException("Logテーブルの構造不一致"))
            .when(metadataService).validateLogTable("companies");

        assertThrows(
            IllegalStateException.class,
            () -> resolver.resolve("companies")
        );
    }

    private List<ColumnMeta> columns() {
        return List.of(
            new ColumnMeta(
                "company_id", "int", 4, 10, 0,
                false, true, false
            ),
            new ColumnMeta(
                "name", "nvarchar", 510, 0, 0,
                true, false, false
            )
        );
    }
}