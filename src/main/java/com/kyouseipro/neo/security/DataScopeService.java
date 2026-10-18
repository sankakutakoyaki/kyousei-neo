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
public class DataScopeService {

    private final SqlRepository sqlRepository;

    private static final Set<String> SCOPES =
        Set.of("ALL", "OWN", "ASSIGNED", "OWN_ASSIGNED");

    public String getScope(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        List<String> roles = authentication.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .filter(role -> role.startsWith("APPROLE_"))
            .distinct()
            .toList();

        if (roles.contains("APPROLE_admin")) {
            return "ALL";
        }

        if (roles.isEmpty()) {
            return null;
        }

        String placeholders = String.join(
            ",", java.util.Collections.nCopies(roles.size(), "?")
        );

        String sql = """
            SELECT DISTINCT s.scope_code AS scopeCode
            FROM dbo.role_data_scopes s
            JOIN dbo.app_roles r ON r.role_id = s.role_id
            WHERE r.role_code IN (%s)
              AND r.state = 0
              AND s.state = 0
            """.formatted(placeholders);

        List<Object> params = new java.util.ArrayList<>(roles);

        List<String> scopes = sqlRepository.selectMap(sql, params).stream()
            .map(row -> String.valueOf(row.get("scopeCode")))
            .filter(SCOPES::contains)
            .toList();

        if (scopes.contains("ALL")) return "ALL";

        boolean own = scopes.contains("OWN")
            || scopes.contains("OWN_ASSIGNED");
        boolean assigned = scopes.contains("ASSIGNED")
            || scopes.contains("OWN_ASSIGNED");

        if (own && assigned) return "OWN_ASSIGNED";
        if (own) return "OWN";
        if (assigned) return "ASSIGNED";

        return null;
    }
}