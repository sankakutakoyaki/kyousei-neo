package com.kyouseipro.neo.controller.page;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.service.dto.HistoryService;
import com.kyouseipro.neo.service.personnel.EmployeeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class IndexController {
	private final EmployeeService employeeService;
    private final HistoryService historyService;

	/**
	 * スタートページ
	 * @param mv
	 * @return
	 */
	@GetMapping("/")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	// public ModelAndView getIndex(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
	// 	mv.setViewName("layouts/main");
	// 	mv.addObject("title", "ホーム");
    //     mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
	// 	mv.addObject("sidebarFragmentName", "fragments/common/menu :: homeFragment");
    //     mv.addObject("bodyFragmentName", "contents/index/home :: bodyFragment");
	// 	mv.addObject("insertCss", "/css/index/home.css");
	// 	// ユーザー名
	// 	String userName = principal.getAttribute("preferred_username");

	// 	EmployeeEntity loginUser = employeeService.getByAccount(userName)
	// 		.orElseThrow(() -> new IllegalStateException("ユーザーが存在しません"));
	// 	// セッションに保持
	// 	session.setAttribute("loginUser", loginUser);
    //     mv.addObject("user", loginUser);

	// 	historyService.save(loginUser.getAccount(), "", "ログイン", 200, "ログインしました。");

    //     return mv;
    // }
	// public ModelAndView getIndex(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
	// 	// ユーザー名
	// 	String userName = principal.getAttribute("preferred_username");
	// 	EmployeeEntity loginUser = employeeService.getByAccount(userName)
	// 		.orElseThrow(() -> new IllegalStateException("ユーザーが存在しません"));
	// 	// セッションに保持
	// 	session.setAttribute("loginUser", loginUser);
    //     mv.addObject("user", loginUser);

	// 	mv.setViewName("contents/index/home");
	// 	mv.addObject("title", "ホーム");

	// 	historyService.save(loginUser.getAccount(), "", "ログイン", 200, "ログインしました。");

	// 	return mv;
    //     // return "contents/index/home";
    // }
	public String getIndex(Model model, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity loginUser = employeeService.getByAccount(userName)
			.orElseThrow(() -> new IllegalStateException("ユーザーが存在しません"));
		// セッションに保持
		session.setAttribute("loginUser", loginUser);
        model.addAttribute("user", loginUser);

		model.addAttribute("title", "ホーム");

		historyService.save(loginUser.getAccount(), "", "ログイン", 200, "ログインしました。");

        return "contents/index/home";
    }
	/**
	 * ログアウト
	 * @param httpRequest
	 * @param response
	 * @throws IOException
	 */
	@PostMapping("/user/logout")
	@ResponseBody
    public void logOut(HttpServletRequest httpRequest, HttpServletResponse response, @AuthenticationPrincipal OidcUser principal, HttpSession session) throws IOException {
        httpRequest.getSession().invalidate();
        String endSessionEndpoint = "https://login.microsoftonline.com/common/oauth2/v2.0/logout";
		String redirectUrl = "https://www.kyouseipro.com/";
		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		historyService.save(userName, "", "ログアウト", 200, "ログアウトしました。");
		response.sendRedirect(endSessionEndpoint + "?post_logout_redirect_uri=" + URLEncoder.encode(redirectUrl, "UTF-8"));
    }
}
