package com.kyouseipro.neo.security;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/permissions")
@PreAuthorize("hasAuthority('APPROLE_admin')")
public class PermissionManagementController {

    private final PermissionManagementService service;

    @GetMapping("/roles")
    public List<Map<String, Object>> roles() {
        return service.findRoles();
    }

    @GetMapping("/roles/{roleId}")
    public Map<String, Object> detail(@PathVariable long roleId) {
        return service.findDetail(roleId);
    }

    @PutMapping("/roles/{roleId}")
    public Map<String, Object> save(
            @PathVariable long roleId,
            @RequestBody PermissionManagementRequest request) {

        return service.save(roleId, request);
    }
}