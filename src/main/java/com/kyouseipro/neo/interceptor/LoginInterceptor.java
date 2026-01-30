package com.kyouseipro.neo.interceptor;

import com.kyouseipro.neo.common.exception.SessionExpiredException;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        HttpSession session = request.getSession(false);
        EmployeeEntity user = (session != null) ? (EmployeeEntity) session.getAttribute("loginUser") : null;

        if (user == null) {
            String uri = request.getRequestURI();
            if (uri.startsWith("/api/")) {
                throw new SessionExpiredException(); // API用
            } else {
                response.sendRedirect("/"); // 画面系用
            }
            return false; // Controller に渡さない
        }
        return true;
    }
}