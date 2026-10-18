package com.kyouseipro.neo.security;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    @GetMapping("/api/session/info")
    public Map<String, Object> sessionInfo(Authentication authentication) {
        boolean keepAlive = authentication.getAuthorities().stream()
            .anyMatch(a -> "APPROLE_office".equals(a.getAuthority()));

        return Map.of("keepAlive", keepAlive);
    }

    @GetMapping("/api/session/keepalive")
    @PreAuthorize("hasAuthority('APPROLE_office')")
    public Map<String, Object> keepalive() {
        return Map.of("ok", true);
    }
}