package com.kyouseipro.neo.security.interceptor;

import com.kyouseipro.neo._backup.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.common.exception.SessionExpiredException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String uri = request.getRequestURI();
        // SSL認証用
        if (uri.startsWith("/.well-known/pki-validation/")) {
            return true;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean unauthenticated = auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken;

        if (unauthenticated) {
            if (uri.startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                response.sendRedirect("/");
            }
            return false;
        }
        return true;
    }
}

// @Component
// public class LoginInterceptor implements HandlerInterceptor {

//     @Override
//     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

//         String uri = request.getRequestURI();

//         // ★ SSL認証用は完全スルー
//         if (uri.startsWith("/.well-known/pki-validation/")) {
//             return true;
//         }

//         HttpSession session = request.getSession(false);
//         // EmployeeEntity user = (session != null) ? (EmployeeEntity) session.getAttribute("loginUser") : null;
//         Object loginUser = (session != null) ? session.getAttribute("loginUser") : null;

//         if (loginUser == null) {
//             if (uri.startsWith("/api/")) {
//                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                 return false;
//             // }
//             // if (uri.startsWith("/api/")) {
//             //     throw new SessionExpiredException();
//             } else {
//                 response.sendRedirect("/");
//             }
//             return false;
//         }

//         // request.setAttribute("loginUser", user);

//         return true;
//     }
// }