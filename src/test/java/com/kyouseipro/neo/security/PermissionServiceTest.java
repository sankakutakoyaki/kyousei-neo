package com.kyouseipro.neo.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.kyouseipro.neo.sql.repository.SqlRepository;

class PermissionServiceTest {

    private SqlRepository repository;
    private PermissionService service;

    @BeforeEach
    void setUp() {
        repository = mock(SqlRepository.class);
        service = new PermissionService(repository);
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

    @Test
    void adminCanAccessWithoutDatabaseLookup() {
        assertTrue(service.canTable(
            auth("APPROLE_admin"), "item_masters", "delete"
        ));

        verifyNoInteractions(repository);
    }

    @Test
    void permittedRoleCanAccess() {
        when(repository.selectMap(anyString(), anyList()))
            .thenReturn(List.of(Map.of("permissionId", 1L)));

        assertTrue(service.canTable(
            auth("APPROLE_master"), "item_masters", "update"
        ));

        verify(repository).selectMap(
            contains("p.can_update = 1"),
            eq(List.of("APPROLE_master", "item_masters"))
        );
    }

    @Test
    void roleWithoutPermissionIsDenied() {
        when(repository.selectMap(anyString(), anyList()))
            .thenReturn(List.of());

        assertFalse(service.canTable(
            auth("APPROLE_user"), "item_masters", "delete"
        ));
    }

    @Test
    void multipleRolesAllowIfAnyRolePermitted() {
        when(repository.selectMap(anyString(), anyList()))
            .thenReturn(List.of(Map.of("permissionId", 1L)));

        assertTrue(service.canTable(
            auth("APPROLE_user", "APPROLE_leader"),
            "item_masters",
            "update"
        ));
    }

    @Test
    void unauthenticatedUserIsDenied() {
        assertFalse(service.canTable(null, "item_masters", "select"));

        Authentication anonymous = new AnonymousAuthenticationToken(
            "key",
            "anonymousUser",
            List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );

        assertFalse(service.canTable(
            anonymous, "item_masters", "select"
        ));

        verifyNoInteractions(repository);
    }

    @Test
    void invalidTableAndActionAreDenied() {
        Authentication admin = auth("APPROLE_admin");

        assertFalse(service.canTable(admin, "item_masters; DROP TABLE", "select"));
        assertFalse(service.canTable(admin, "item_masters", "execute"));
        assertFalse(service.canTable(admin, null, "select"));

        verifyNoInteractions(repository);
    }

    @Test
    void userWithoutApplicationRoleIsDenied() {
        assertFalse(service.canTable(
            auth("OIDC_USER"), "item_masters", "select"
        ));

        verifyNoInteractions(repository);
    }

    @Test
    void adminCanExecuteOperation() {
        assertTrue(service.canOperation(
            auth("APPROLE_admin"), "dispatch.confirm"
        ));
        verifyNoInteractions(repository);
    }

    @Test
    void permittedRoleCanExecuteOperation() {
        when(repository.selectMap(anyString(), anyList()))
            .thenReturn(List.of(Map.of("permissionId", 1L)));

        assertTrue(service.canOperation(
            auth("APPROLE_leader"), "dispatch.confirm"
        ));

        verify(repository).selectMap(
            contains("p.allowed = 1"),
            eq(List.of("APPROLE_leader", "dispatch.confirm"))
        );
    }

    @Test
    void operationWithoutPermissionIsDenied() {
        when(repository.selectMap(anyString(), anyList()))
            .thenReturn(List.of());

        assertFalse(service.canOperation(
            auth("APPROLE_user"), "dispatch.confirm"
        ));
    }

    @Test
    void invalidOperationIsDenied() {
        assertFalse(service.canOperation(null, "dispatch.confirm"));
        assertFalse(service.canOperation(
            auth("APPROLE_admin"), "dispatch.confirm; DROP TABLE"
        ));
        assertFalse(service.canOperation(
            auth("APPROLE_admin"), null
        ));

        verifyNoInteractions(repository);
    }
}