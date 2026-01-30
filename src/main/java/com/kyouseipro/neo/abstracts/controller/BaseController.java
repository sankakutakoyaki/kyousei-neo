package com.kyouseipro.neo.abstracts.controller;

import com.kyouseipro.neo.common.exception.SessionExpiredException;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;

public abstract class BaseController {

    /**
     * 画面系用: セッションからユーザーを取得。
     * ログインしていなければ / にリダイレクト。
     */
    protected EmployeeEntity getLoginUser(HttpSession session, HttpServletResponse response) throws IOException {

        EmployeeEntity user = (EmployeeEntity) session.getAttribute("loginUser");
        if (user == null) {
            response.sendRedirect("/"); // indexへ
            return null;
        }
        return user;
    }

    /**
     * API系用: セッションからユーザーを取得。
     * ログインしていなければ例外を投げる。
     */
    protected EmployeeEntity getLoginUser(HttpSession session) {

        EmployeeEntity user = (EmployeeEntity) session.getAttribute("loginUser");
        if (user == null) {
            throw new SessionExpiredException();
        }
        return user;
    }

    /**
     * 画面系用: Thymeleaf に user を自動注入
     */
    @ModelAttribute("user")
    public EmployeeEntity injectUser(HttpSession session) {
        // session がなければ null を返す
        return (EmployeeEntity) session.getAttribute("loginUser");
    }
}
