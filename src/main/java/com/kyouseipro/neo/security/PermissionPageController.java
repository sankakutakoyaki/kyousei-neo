package com.kyouseipro.neo.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PermissionPageController {

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('APPROLE_admin')")
    public String getPermissions() {
        return "fragments/pages/security/permission/content :: content";
    }
}