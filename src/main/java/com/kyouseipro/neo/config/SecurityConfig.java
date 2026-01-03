// package com.kyouseipro.neo.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//         http
//             // CSRF 無効（REST API 向け、必要に応じて調整）
//             .csrf(csrf -> csrf.disable())

//             // 静的リソースは許可
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()

//                 // REST API は /api/** 配下のみ認証必須
//                 .requestMatchers("/api/**").authenticated()

//                 // 画面系ページも認証必須
//                 .anyRequest().authenticated()
//             )

//             // OAuth2/OIDC ログイン設定
//             .oauth2Login(oauth -> oauth
//                 .loginPage("/") // ログインはトップにリダイレクト（Azure Entra ID 画面）
//             )

//             // ログアウト処理
//             .logout(logout -> logout
//                 .logoutUrl("/user/logout")
//                 .logoutSuccessHandler(oidcLogoutSuccessHandler())
//             );

//         return http.build();
//     }

//     /**
//      * Azure OIDC ログアウト時に redirect
//      */
//     private LogoutSuccessHandler oidcLogoutSuccessHandler() {
//         return (request, response, authentication) -> {
//             // セッション破棄
//             request.getSession().invalidate();

//             // Azure Entra ID のログアウト URL
//             String endSessionEndpoint = "https://login.microsoftonline.com/common/oauth2/v2.0/logout";
//             String redirectUrl = "https://www.kyouseipro.com/";

//             response.sendRedirect(endSessionEndpoint + "?post_logout_redirect_uri=" + java.net.URLEncoder.encode(redirectUrl, java.nio.charset.StandardCharsets.UTF_8));
//         };
//     }
// }