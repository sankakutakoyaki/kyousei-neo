package com.kyouseipro.neo.sql.common;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SqlBuilderTest {

    @Test
    void acceptsValidColumnNames() {
        assertEquals("company_name", SqlBuilder.toSnake("companyName"));
        assertEquals("daily_crew_id", SqlBuilder.toSnake("dailyCrewId"));
        assertEquals("recycle_number", SqlBuilder.toSnake("recycle_number"));
        assertEquals("state", SqlBuilder.toSnake("state"));
    }

    @Test
    void rejectsInvalidColumnNames() {
        assertThrows(IllegalArgumentException.class, () -> SqlBuilder.toSnake("companyName;DROP"));
        assertThrows(IllegalArgumentException.class, () -> SqlBuilder.toSnake("company name"));
        assertThrows(IllegalArgumentException.class, () -> SqlBuilder.toSnake("company-name"));
        assertThrows(IllegalArgumentException.class, () -> SqlBuilder.toSnake(null));
    }
}