package com.kyouseipro.neo.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.repository.SqlRepository;

class PermissionManagementConflictTest {

    private SqlRepository repository;
    private PermissionManagementService service;

    @BeforeEach
    void setUp() {
        repository = mock(SqlRepository.class);
        service = new PermissionManagementService(repository);
    }

    @Test
    void staleRoleVersionShouldRejectSave() {
        long roleId = 1003L;

        // DBの現在versionは5、画面が保持しているversionは4
        when(repository.selectMap(anyString(), any()))
            .thenAnswer(invocation -> {
                String sql = invocation.getArgument(0);

                if (sql.contains("FROM dbo.app_roles")
                        && sql.contains("WHERE role_id = ?")) {
                    return List.of(Map.of(
                        "roleId", roleId,
                        "roleCode", "APPROLE_staff",
                        "roleName", "事務担当者",
                        "version", 5
                    ));
                }

                if (sql.contains("FROM dbo.role_table_permissions p")
                        && sql.contains("APPROLE_master")) {
                    return List.of(Map.of(
                        "tableName", "orders",
                        "canSelect", 1,
                        "canInsert", 0,
                        "canUpdate", 0,
                        "canDelete", 0
                    ));
                }

                if (sql.contains("FROM dbo.role_operation_permissions p")
                        && sql.contains("APPROLE_master")) {
                    return List.of(Map.of(
                        "operationCode", "order.save",
                        "allowed", 0
                    ));
                }

                return List.of();
            });

        // UPDATE ... WHERE version = 4 は0件となり競合
        doThrow(new BusinessException(
            "権限設定が変更されています。再読み込みしてください。"
        )).when(repository).updateRequired(
            anyString(),
            eq(List.of(roleId, 4)),
            anyString()
        );

        var request = new PermissionManagementRequest(
            4,
            "OWN",
            null,
            null,
            List.of(new PermissionManagementRequest.TablePermission(
                "orders", null, null, true, false, false, false
            )),
            List.of(new PermissionManagementRequest.OperationPermission(
                "order.save", null, null, false
            ))
        );

        assertThrows(BusinessException.class,
            () -> service.save(roleId, request));

        // ロールの競合検出時点で停止し、
        // 権限テーブルには書き込まない
        verify(repository, times(1)).updateRequired(
            anyString(),
            eq(List.of(roleId, 4)),
            anyString()
        );
        verify(repository, never()).update(
            anyString(), any(List.class)
        );
    }
}