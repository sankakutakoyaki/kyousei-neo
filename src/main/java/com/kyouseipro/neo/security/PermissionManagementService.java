package com.kyouseipro.neo.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionManagementService {

    private final SqlRepository repository;

    private static final Set<String> SCOPES =
        Set.of("ALL", "OWN", "ASSIGNED", "OWN_ASSIGNED");

    private static final String CONFLICT =
        "権限設定が変更されています。再読み込みしてください。";

    public List<Map<String, Object>> findRoles() {
        return repository.selectMap("""
            SELECT role_id, role_code, role_name, version
            FROM dbo.app_roles
            WHERE state = 0
            ORDER BY display_order, role_id
            """, List.of());
    }

    public Map<String, Object> findDetail(long roleId) {
        Map<String, Object> role = findRole(roleId);

        Map<String, Object> result = new LinkedHashMap<>(role);
        result.put("tables", findTables(roleId));
        result.put("operations", findOperations(roleId));

        List<Map<String, Object>> scopes = repository.selectMap("""
            SELECT
                role_data_scope_id AS scope_id,
                scope_code,
                version AS scope_version
            FROM dbo.role_data_scopes
            WHERE role_id = ? AND state = 0
            """, List.of(roleId));

        if (scopes.isEmpty()) {
            result.put("scopeCode", null);
            result.put("scopeId", null);
            result.put("scopeVersion", null);
        } else {
            result.putAll(scopes.get(0));
        }

        return result;
    }

    private Map<String, Object> findRole(long roleId) {
        List<Map<String, Object>> rows = repository.selectMap("""
            SELECT role_id, role_code, role_name, version
            FROM dbo.app_roles
            WHERE role_id = ? AND state = 0
            """, List.of(roleId));

        if (rows.isEmpty()) {
            throw new BusinessException("対象のロールが存在しません。");
        }
        return rows.get(0);
    }

    // masterに登録された権限を、管理画面の選択肢として使用する
    private List<Map<String, Object>> findTables(long roleId) {
        return repository.selectMap("""
            SELECT
                c.table_name,
                p.permission_id,
                p.version,
                COALESCE(p.can_select, 0) AS can_select,
                COALESCE(p.can_insert, 0) AS can_insert,
                COALESCE(p.can_update, 0) AS can_update,
                COALESCE(p.can_delete, 0) AS can_delete
            FROM (
                SELECT DISTINCT p.table_name
                FROM dbo.role_table_permissions p
                JOIN dbo.app_roles r ON r.role_id = p.role_id
                WHERE r.role_code = 'APPROLE_master'
                  AND r.state = 0
                  AND p.state = 0
            ) c
            LEFT JOIN dbo.role_table_permissions p
                ON p.table_name = c.table_name
               AND p.role_id = ?
               AND p.state = 0
            ORDER BY c.table_name
            """, List.of(roleId));
    }

    private List<Map<String, Object>> findOperations(long roleId) {
        return repository.selectMap("""
            SELECT
                c.operation_code,
                p.permission_id,
                p.version,
                COALESCE(p.allowed, 0) AS allowed
            FROM (
                SELECT DISTINCT p.operation_code
                FROM dbo.role_operation_permissions p
                JOIN dbo.app_roles r ON r.role_id = p.role_id
                WHERE r.role_code = 'APPROLE_master'
                  AND r.state = 0
                  AND p.state = 0
            ) c
            LEFT JOIN dbo.role_operation_permissions p
                ON p.operation_code = c.operation_code
               AND p.role_id = ?
               AND p.state = 0
            ORDER BY c.operation_code
            """, List.of(roleId));
    }

    @Transactional
    public Map<String, Object> save(
            long roleId,
            PermissionManagementRequest request) {

        if (request == null) {
            throw new BusinessException("権限設定が指定されていません。");
        }

        Map<String, Object> role = findRole(roleId);

        if ("APPROLE_admin".equals(role.get("roleCode"))) {
            throw new AccessDeniedException("adminの権限は変更できません。");
        }

        validateRequest(roleId, request);

        // ロール単位の競合確認と更新の直列化
        repository.updateRequired("""
            UPDATE dbo.app_roles
            SET version = version + 1,
                update_date = SYSDATETIME()
            WHERE role_id = ?
              AND version = ?
              AND state = 0
            """,
            List.of(roleId, request.version()),
            CONFLICT
        );

        saveScope(roleId, request);
        saveTables(roleId, request.tables());
        saveOperations(roleId, request.operations());

        return findDetail(roleId);
    }

    private void validateRequest(
            long roleId,
            PermissionManagementRequest request) {

        if (request.scopeCode() == null
                || !SCOPES.contains(request.scopeCode())) {
            throw new BusinessException("アクセス範囲が不正です。");
        }

        if (request.tables() == null || request.operations() == null) {
            throw new BusinessException("権限設定が不足しています。");
        }

        // 送信された項目が、定義済みの項目と完全一致するか確認
        Set<String> tableNames = new HashSet<>();
        for (var item : request.tables()) {
            if (item == null || item.tableName() == null
                    || !tableNames.add(item.tableName())) {
                throw new BusinessException("テーブル権限に重複または不正な項目があります。");
            }
        }

        Set<String> operationCodes = new HashSet<>();
        for (var item : request.operations()) {
            if (item == null || item.operationCode() == null
                    || !operationCodes.add(item.operationCode())) {
                throw new BusinessException("操作権限に重複または不正な項目があります。");
            }
        }

        Set<String> validTables = new HashSet<>();
        for (var row : findTables(roleId)) {
            validTables.add((String) row.get("tableName"));
        }

        Set<String> validOperations = new HashSet<>();
        for (var row : findOperations(roleId)) {
            validOperations.add((String) row.get("operationCode"));
        }

        if (!tableNames.equals(validTables)
                || !operationCodes.equals(validOperations)
                || validTables.isEmpty()
                || validOperations.isEmpty()) {
            throw new BusinessException("権限定義が一致しません。再読み込みしてください。");
        }
    }

    private void saveScope(long roleId, PermissionManagementRequest request) {
        List<Map<String, Object>> rows = repository.selectMap("""
            SELECT
                role_data_scope_id AS scope_id,
                scope_code,
                version AS scope_version
            FROM dbo.role_data_scopes
            WHERE role_id = ? AND state = 0
            """, List.of(roleId));

        if (request.scopeId() == null) {
            if (request.scopeVersion() != null || !rows.isEmpty()) {
                throw new BusinessException(CONFLICT);
            }

            // 削除済みの設定が存在する場合は新規登録しない
            List<Map<String, Object>> existing = repository.selectMap("""
                SELECT role_data_scope_id
                FROM dbo.role_data_scopes
                WHERE role_id = ?
                """, List.of(roleId));

            if (!existing.isEmpty()) {
                throw new BusinessException(CONFLICT);
            }

            repository.update("""
                INSERT INTO dbo.role_data_scopes
                    (role_id, scope_code, state, version)
                VALUES (?, ?, 0, 0)
                """, List.of(roleId, request.scopeCode()));
            return;
        }

        if (rows.size() != 1) {
            throw new BusinessException(CONFLICT);
        }

        Map<String, Object> current = rows.get(0);

        checkId(current.get("scopeId"), request.scopeId());
        checkVersion(current.get("scopeVersion"), request.scopeVersion());

        // 設定値が変わっていなければUPDATE不要
        if (request.scopeCode().equals(current.get("scopeCode"))) {
            return;
        }

        repository.updateRequired("""
            UPDATE dbo.role_data_scopes
            SET scope_code = ?,
                version = version + 1,
                updated_at = SYSDATETIME()
            WHERE role_data_scope_id = ?
            AND role_id = ?
            AND version = ?
            AND state = 0
            """,
            List.of(
                request.scopeCode(),
                request.scopeId(),
                roleId,
                request.scopeVersion()
            ),
            CONFLICT
        );
    }

    private void saveTables(
            long roleId,
            List<PermissionManagementRequest.TablePermission> tables) {

        Map<String, Map<String, Object>> current = new HashMap<>();

        for (Map<String, Object> row : findTables(roleId)) {
            current.put((String) row.get("tableName"), row);
        }

        for (var item : tables) {
            Map<String, Object> old = current.get(item.tableName());

            if (old == null) {
                throw new BusinessException(CONFLICT);
            }

            if (item.permissionId() == null) {
                if (item.version() != null || old.get("permissionId") != null) {
                    throw new BusinessException(CONFLICT);
                }

                repository.update("""
                    INSERT INTO dbo.role_table_permissions
                        (role_id, table_name,
                        can_select, can_insert, can_update, can_delete,
                        regist_date, update_date, version, state)
                    VALUES (?, ?, ?, ?, ?, ?, SYSDATETIME(), SYSDATETIME(), 0, 0)
                    """,
                    List.of(
                        roleId,
                        item.tableName(),
                        item.canSelect(),
                        item.canInsert(),
                        item.canUpdate(),
                        item.canDelete()
                    )
                );
                continue;
            }

            checkId(old.get("permissionId"), item.permissionId());
            checkVersion(old.get("version"), item.version());

            boolean unchanged =
                    item.canSelect() == isAllowed(old.get("canSelect"))
                    && item.canInsert() == isAllowed(old.get("canInsert"))
                    && item.canUpdate() == isAllowed(old.get("canUpdate"))
                    && item.canDelete() == isAllowed(old.get("canDelete"));

            if (unchanged) {
                continue;
            }

            repository.updateRequired("""
                UPDATE dbo.role_table_permissions
                SET can_select = ?,
                    can_insert = ?,
                    can_update = ?,
                    can_delete = ?,
                    version = version + 1,
                    update_date = SYSDATETIME()
                WHERE permission_id = ?
                AND role_id = ?
                AND table_name = ?
                AND version = ?
                AND state = 0
                """,
                List.of(
                    item.canSelect(),
                    item.canInsert(),
                    item.canUpdate(),
                    item.canDelete(),
                    item.permissionId(),
                    roleId,
                    item.tableName(),
                    item.version()
                ),
                CONFLICT
            );
        }
    }

    private void saveOperations(
            long roleId,
            List<PermissionManagementRequest.OperationPermission> operations) {

        Map<String, Map<String, Object>> current = new HashMap<>();

        for (Map<String, Object> row : findOperations(roleId)) {
            current.put((String) row.get("operationCode"), row);
        }

        for (var item : operations) {
            Map<String, Object> old = current.get(item.operationCode());

            if (old == null) {
                throw new BusinessException(CONFLICT);
            }

            if (item.permissionId() == null) {
                if (item.version() != null || old.get("permissionId") != null) {
                    throw new BusinessException(CONFLICT);
                }

                repository.update("""
                    INSERT INTO dbo.role_operation_permissions
                        (role_id, operation_code, allowed,
                        regist_date, update_date, version, state)
                    VALUES (?, ?, ?, SYSDATETIME(), SYSDATETIME(), 0, 0)
                    """,
                    List.of(
                        roleId,
                        item.operationCode(),
                        item.allowed()
                    )
                );
                continue;
            }

            checkId(old.get("permissionId"), item.permissionId());
            checkVersion(old.get("version"), item.version());

            if (item.allowed() == isAllowed(old.get("allowed"))) {
                continue;
            }

            repository.updateRequired("""
                UPDATE dbo.role_operation_permissions
                SET allowed = ?,
                    version = version + 1,
                    update_date = SYSDATETIME()
                WHERE permission_id = ?
                AND role_id = ?
                AND operation_code = ?
                AND version = ?
                AND state = 0
                """,
                List.of(
                    item.allowed(),
                    item.permissionId(),
                    roleId,
                    item.operationCode(),
                    item.version()
                ),
                CONFLICT
            );
        }
    }
    private boolean isAllowed(Object value) {
        if (value instanceof Boolean b) return b;
        if (value instanceof Number n) return n.intValue() != 0;
        return false;
    }

    private void checkVersion(Object current, Integer expected) {
        if (!(current instanceof Number number)
                || expected == null
                || number.intValue() != expected) {
            throw new BusinessException(CONFLICT);
        }
    }

    private void checkId(Object current, Long expected) {
        if (!(current instanceof Number number)
                || expected == null
                || number.longValue() != expected) {
            throw new BusinessException(CONFLICT);
        }
    }
}