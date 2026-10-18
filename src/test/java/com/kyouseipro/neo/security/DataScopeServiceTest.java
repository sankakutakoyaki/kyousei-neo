package com.kyouseipro.neo.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.kyouseipro.neo.sql.repository.SqlRepository;

class DataScopeServiceTest {

    private SqlRepository sqlRepository;
    private DataScopeService service;

    @BeforeEach
    void setUp() {
        sqlRepository = mock(SqlRepository.class);
        service = new DataScopeService(sqlRepository);
    }

    private Authentication auth(String... roles) {
        return new UsernamePasswordAuthenticationToken(
            "test@example.com",
            null,
            java.util.Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .toList()
        );
    }

    private void mockScopes(String... scopes) {
        List<Map<String, Object>> rows = java.util.Arrays.stream(scopes)
            .map(scope -> Map.<String, Object>of("scopeCode", scope))
            .toList();

        when(sqlRepository.selectMap(anyString(), anyList()))
            .thenReturn(rows);
    }

    @Test
    void adminAlwaysHasAllScope() {
        assertEquals("ALL", service.getScope(auth("APPROLE_admin")));
        verifyNoInteractions(sqlRepository);
    }

    @Test
    void masterUsesDatabaseScope() {
        mockScopes("OWN");

        assertEquals("OWN", service.getScope(auth("APPROLE_master")));
    }

    @Test
    void multipleRolesCombineScopes() {
        mockScopes("OWN", "ASSIGNED");

        assertEquals(
            "OWN_ASSIGNED",
            service.getScope(auth("APPROLE_leader", "APPROLE_user"))
        );
    }

    @Test
    void allScopeTakesPriority() {
        mockScopes("OWN", "ALL");

        assertEquals(
            "ALL",
            service.getScope(auth("APPROLE_master", "APPROLE_staff"))
        );
    }

    @Test
    void missingScopeReturnsNull() {
        mockScopes();

        assertNull(service.getScope(auth("APPROLE_staff")));
    }

    @Test
    void unauthenticatedUserHasNoScope() {
        assertNull(service.getScope(null));
        verifyNoInteractions(sqlRepository);
    }

    @Test
    void nonAppRoleHasNoScope() {
        assertNull(service.getScope(auth("OTHER_ROLE")));
        verifyNoInteractions(sqlRepository);
    }
}