package com.kyouseipro.neo.controller.abstracts;

import com.kyouseipro.neo.common.exception.SessionExpiredException;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;

public abstract class BaseController {

    /**
     * セッションからログインユーザー取得
     * @param session HttpSession
     * @param response HttpServletResponse
     * @param isApi API呼び出しかどうか
     * @return EmployeeEntity or null（処理はリダイレクト or JSON出力済み）
     * @throws IOException
     */
    // protected EmployeeEntity getLoginUser(HttpSession session, HttpServletResponse response, boolean isApi) throws IOException {
    //     EmployeeEntity user = (EmployeeEntity) session.getAttribute("loginUser");
    //     if (user == null) {
    //         if (isApi) {
    //             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    //             response.setContentType("application/json;charset=UTF-8");
    //             response.getWriter().write("{\"success\":false,\"message\":\"session expired\"}");
    //         } else {
    //             response.sendRedirect("/");
    //         }
    //         return null;
    //     }
    //     return user;
    // }

    /**
     * 画面系用: セッションからユーザーを取得。
     * ログインしていなければ / にリダイレクト。
     */
    protected EmployeeEntity getLoginUser(HttpSession session, HttpServletResponse response) throws IOException {
System.out.println("web:" + session);
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
System.out.println("api:" + session);
        EmployeeEntity user = (EmployeeEntity) session.getAttribute("loginUser");
        if (user == null) {
            throw new SessionExpiredException();
        }
        return user;
    }

    // /**
    //  * API系で呼び出し: ログイン必須
    //  */
    // protected void requireLogin(HttpSession session) {
    //     if (getLoginUser(session) == null) {
    //         throw new SessionExpiredException();
    //     }
    // }

    /**
     * 画面系用: Thymeleaf に user を自動注入
     */
    @ModelAttribute("user")
    public EmployeeEntity injectUser(HttpSession session) {
        // session がなければ null を返す
        return (EmployeeEntity) session.getAttribute("loginUser");
    }
}
