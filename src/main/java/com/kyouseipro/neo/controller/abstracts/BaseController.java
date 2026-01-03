package com.kyouseipro.neo.controller.abstracts;

import org.springframework.web.bind.annotation.ModelAttribute;

import com.kyouseipro.neo.entity.personnel.EmployeeEntity;

import jakarta.servlet.http.HttpSession;

public abstract class BaseController {

    protected EmployeeEntity getLoginUser(HttpSession session) {
        EmployeeEntity user =
            (EmployeeEntity) session.getAttribute("loginUser");

        if (user == null) {
            throw new IllegalStateException("セッション切れ");
        }
        return user;
    }

    protected String getLoginUserName(HttpSession session) {
        return getLoginUser(session).getAccount();
    }

    @ModelAttribute("user")
    public EmployeeEntity injectUser(HttpSession session) {
        return getLoginUser(session);
    }
}
