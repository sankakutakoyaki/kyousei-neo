package com.kyouseipro.neo.controller.abstracts;

import org.springframework.web.bind.annotation.ModelAttribute;

import com.kyouseipro.neo.common.exception.SessionExpiredException;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;

import jakarta.servlet.http.HttpSession;

public abstract class BaseController {

    protected EmployeeEntity getLoginUser(HttpSession session) {
        return (EmployeeEntity) session.getAttribute("loginUser");
    }

    protected void requireLogin(HttpSession session) {
        if (getLoginUser(session) == null) {
            throw new SessionExpiredException();
        }
    }

    @ModelAttribute("user")
    public EmployeeEntity injectUser(HttpSession session) {
        return getLoginUser(session);
    }
}
