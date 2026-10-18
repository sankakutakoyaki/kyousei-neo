package com.kyouseipro.neo.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.repository.SqlRepository;

class PermissionManagementServiceTest {

    private SqlRepository repository;
    private PermissionManagementService service;

    @BeforeEach
    void setUp() {
        repository = mock(SqlRepository.class);
        service = new PermissionManagementService(repository);
    }

    private PermissionManagementRequest request(String scope) {
        return new PermissionManagementRequest(
            0,
            scope,
            null,
            null,
            List.of(new PermissionManagementRequest.TablePermission(
                "orders", null, null, true, true, true, false
            )),
            List.of(new PermissionManagementRequest.OperationPermission(
                "order.save", null, null, true
            ))
        );
    }

    private void mockRole(String roleCode) {
        when(repository.selectMap(
            contains("FROM dbo.app_roles"),
            eq(List.of(2L))
        )).thenReturn(List.of(Map.of(
            "roleId", 2L,
            "roleCode", roleCode,
            "roleName", "テストロール",
            "version", 0
        )));
    }

    private void mockCatalog() {
        when(repository.selectMap(
            contains("FROM (\n"),
            anyList()
        )).thenAnswer(invocation -> {
            String sql = invocation.getArgument(0);
            if (sql.contains("role_table_permissions")) {
                return List.of(Map.of("tableName", "orders"));
            }
            return List.of(Map.of("operationCode", "order.save"));
        });
    }

    @Test
    void adminRoleCannotBeChanged() {
        mockRole("APPROLE_admin");

        assertThrows(
            AccessDeniedException.class,
            () -> service.save(2L, request("ALL"))
        );

        verify(repository, never()).updateRequired(
            anyString(), anyList(), anyString()
        );
    }

    @Test
    void invalidScopeIsRejected() {
        mockRole("APPROLE_staff");

        assertThrows(
            BusinessException.class,
            () -> service.save(2L, request("INVALID"))
        );

        verify(repository, never()).updateRequired(
            anyString(), anyList(), anyString()
        );
    }

    @Test
    void unknownPermissionIsRejected() {
        mockRole("APPROLE_staff");
        mockCatalog();

        var invalid = new PermissionManagementRequest(
            0, "OWN", null, null,
            List.of(new PermissionManagementRequest.TablePermission(
                "unknown_table", null, null, true, false, false, false
            )),
            request("OWN").operations()
        );

        assertThrows(
            BusinessException.class,
            () -> service.save(2L, invalid)
        );
    }

    @Test
    void validPermissionsAreSaved() {
        mockRole("APPROLE_staff");
        mockCatalog();

        service.save(2L, request("OWN"));

        verify(repository).updateRequired(
            contains("UPDATE dbo.app_roles"),
            eq(List.of(2L, 0)),
            anyString()
        );

        verify(repository).update(
            contains("INSERT INTO dbo.role_data_scopes"),
            anyList()
        );

        verify(repository).update(
            contains("INSERT INTO dbo.role_table_permissions"),
            anyList()
        );

        verify(repository).update(
            contains("INSERT INTO dbo.role_operation_permissions"),
            anyList()
        );
    }
}