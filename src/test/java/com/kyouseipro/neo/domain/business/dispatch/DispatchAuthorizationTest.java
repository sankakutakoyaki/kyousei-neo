package com.kyouseipro.neo.domain.business.dispatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import jakarta.servlet.http.HttpServletRequest;

class DispatchAuthorizationTest {
    @Configuration @EnableMethodSecurity
    static class Config {
        @Bean SqlRepository sql() { return mock(SqlRepository.class); }
        @Bean HttpServletRequest request() { return mock(HttpServletRequest.class); }
        @Bean DispatchService dispatch(SqlRepository sql, HttpServletRequest request) { return new DispatchService(sql, new DispatchLogSqlProvider(), request); }
    }
    @Test void queryServiceEnforcesRolesOnReadsAndWrites() {
        try (var context = new AnnotationConfigApplicationContext(Config.class)) {
            var service = context.getBean(DispatchService.class);
            for (String role : List.of("APPROLE_office", "unrelated")) {
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user","",List.of(new SimpleGrantedAuthority(role))));
                assertThrows(AccessDeniedException.class, service::employees);
                assertThrows(AccessDeniedException.class, () -> service.list(Map.of()));
                assertThrows(AccessDeniedException.class, () -> service.detail(Map.of()));
                assertThrows(AccessDeniedException.class, () -> service.save(Map.of()));
            }
            verifyNoInteractions(context.getBean(SqlRepository.class));
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user","",List.of(new SimpleGrantedAuthority("APPROLE_user"))));
            assertDoesNotThrow(service::employees);
        } finally { SecurityContextHolder.clearContext(); }
    }
}
