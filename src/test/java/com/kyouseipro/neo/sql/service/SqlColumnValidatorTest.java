package com.kyouseipro.neo.sql.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SqlColumnValidatorTest {

    private QueryExecutor queryExecutor;
    private SqlColumnValidator validator;

    @BeforeEach
    void setUp() {
        queryExecutor = mock(QueryExecutor.class);
        validator = new SqlColumnValidator(queryExecutor);

        when(queryExecutor.select(any(), any())).thenReturn(List.of(
            Map.of("columnName", "company_id"),
            Map.of("columnName", "company_name"),
            Map.of("columnName", "postal_code"),
            Map.of("columnName", "state"),
            Map.of("columnName", "version")
        ));
    }

    @Test
    void acceptsExistingColumns() {
        assertDoesNotThrow(() -> validator.validate(
            "companies", Set.of("companyName", "postalCode")
        ));
    }

    @Test
    void rejectsUnknownColumns() {
        assertThrows(IllegalArgumentException.class, () ->
            validator.validate("companies", Set.of("unknownColumn"))
        );
    }

    @Test
    void rejectsInvalidColumnNames() {
        assertThrows(IllegalArgumentException.class, () ->
            validator.validate("companies", Set.of("companyName;DROP"))
        );
    }

    @Test
    void acceptsSnakeCaseColumns() {
        assertDoesNotThrow(() ->
            validator.validate("companies", Set.of("company_name"))
        );
    }

    @Test
    void validatesRequestedTableName() {
        validator.validate("companies", Set.of("companyName"));

        verify(queryExecutor).select(
            any(),
            argThat(req ->
                "companies".equals(req.getParams().get("tableName"))
                && "dbo".equals(req.getParams().get("schemaName"))
            )
        );
    }
}