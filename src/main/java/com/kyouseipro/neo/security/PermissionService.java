package com.kyouseipro.neo.security;

import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final SqlRepository sqlRepository;

    private static final Set<String> ACTIONS =
        Set.of("select", "insert", "update", "delete");

    public boolean canTable(
            Authentication authentication,
            String tableName,
            String action) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        if (tableName == null || !tableName.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            return false;
        }

        if (!ACTIONS.contains(action)) {
            return false;
        }

        List<String> roles = authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .filter(role -> role.startsWith("APPROLE_"))
            .toList();

        if (roles.contains("APPROLE_admin")) {
            return true;
        }

        if (roles.isEmpty()) {
            return false;
        }

        String sql = """
            SELECT TOP (1) p.permission_id
            FROM dbo.role_table_permissions p
            JOIN dbo.app_roles r ON r.role_id = p.role_id
            WHERE r.role_code IN (%s)
              AND r.state = 0
              AND p.state = 0
              AND p.table_name = ?
              AND p.can_%s = 1
            """.formatted(
                String.join(",", java.util.Collections.nCopies(roles.size(), "?")),
                action
            );

        List<Object> params = new java.util.ArrayList<>(roles);
        params.add(tableName);

        return !sqlRepository.selectMap(sql, params).isEmpty();
    }

    public boolean canOperation(
            Authentication authentication,
            String operationCode) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        if (operationCode == null
                || !operationCode.matches("[A-Za-z][A-Za-z0-9_.-]*")) {
            return false;
        }

        List<String> roles = authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .filter(role -> role.startsWith("APPROLE_"))
            .distinct()
            .toList();

        if (roles.contains("APPROLE_admin")) {
            return true;
        }

        if (roles.isEmpty()) {
            return false;
        }

        String sql = """
            SELECT TOP (1) p.permission_id
            FROM dbo.role_operation_permissions p
            JOIN dbo.app_roles r ON r.role_id = p.role_id
            WHERE r.role_code IN (%s)
            AND r.state = 0
            AND p.state = 0
            AND p.operation_code = ?
            AND p.allowed = 1
            """.formatted(
                String.join(",", java.util.Collections.nCopies(roles.size(), "?"))
            );

        List<Object> params = new java.util.ArrayList<>(roles);
        params.add(operationCode);

        return !sqlRepository.selectMap(sql, params).isEmpty();
    }
}