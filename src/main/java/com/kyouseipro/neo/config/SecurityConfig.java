package com.kyouseipro.neo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.kyouseipro.neo.security.ApiAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    RequestCache loginRequestCache() {
        HttpSessionRequestCache cache = new HttpSessionRequestCache();
        cache.setRequestMatcher(request -> {
            String path = request.getServletPath();
            String accept = request.getHeader("Accept");
            return "GET".equals(request.getMethod()) && accept != null && accept.contains("text/html")
                && !path.startsWith("/api/") && !path.equals("/sw.js")
                && !path.startsWith("/js/") && !path.startsWith("/css/")
                && !path.startsWith("/icons/") && !path.startsWith("/.well-known/");
        });
        return cache;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ApiAuthenticationEntryPoint apiEntryPoint, RequestCache loginRequestCache) throws Exception {
        http
            .headers(headers ->
                headers.frameOptions(frame ->
                    frame.sameOrigin()
                )
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login/**",
                    "/oauth2/**",
                    "/error",
                    "/sw.js",
                    "/.well-known/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
            .defaultAuthenticationEntryPointFor(apiEntryPoint, new AntPathRequestMatcher("/api/**")))
            .requestCache(cache -> cache.requestCache(loginRequestCache))
            .oauth2Login(Customizer.withDefaults())
            .oauth2Client(Customizer.withDefaults());

        return http.build();
    }
}
