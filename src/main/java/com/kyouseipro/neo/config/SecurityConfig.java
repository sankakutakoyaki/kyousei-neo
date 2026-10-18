package com.kyouseipro.neo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.kyouseipro.neo.security.ApiAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ApiAuthenticationEntryPoint apiEntryPoint) throws Exception {
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
                    "/.well-known/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
            .defaultAuthenticationEntryPointFor(apiEntryPoint, new AntPathRequestMatcher("/api/**")))
            .oauth2Login(Customizer.withDefaults())
            .oauth2Client(Customizer.withDefaults());

        return http.build();
    }
}
