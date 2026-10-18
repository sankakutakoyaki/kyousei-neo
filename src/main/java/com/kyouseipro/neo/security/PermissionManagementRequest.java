package com.kyouseipro.neo.security;

import java.util.List;

public record PermissionManagementRequest(
    int version,
    String scopeCode,
    Long scopeId,
    Integer scopeVersion,
    List<TablePermission> tables,
    List<OperationPermission> operations
) {
    public record TablePermission(
        String tableName,
        Long permissionId,
        Integer version,
        boolean canSelect,
        boolean canInsert,
        boolean canUpdate,
        boolean canDelete
    ) {}

    public record OperationPermission(
        String operationCode,
        Long permissionId,
        Integer version,
        boolean allowed
    ) {}
}