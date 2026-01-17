package com.kyouseipro.neo.controller.page;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
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
	public ModelAndView getIndex(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
		mv.setViewName("layouts/main");
		mv.addObject("title", "ホーム");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: homeFragment");
        mv.addObject("bodyFragmentName", "contents/index/home :: bodyFragment");
		mv.addObject("insertCss", "/css/index/home.css");
		// ユーザー名
		String userName = principal.getAttribute("preferred_username");

		EmployeeEntity loginUser = employeeService.getByAccount(userName)
			.orElseThrow(() -> new IllegalStateException("ユーザーが存在しません"));
		// セッションに保持
		session.setAttribute("loginUser", loginUser);

		// // EmployeeEntity entity = employeeService.getByAccount(userName).orElse(null);
		// // mv.addObject("user", employeeService.getByAccount(userName).orElse(null));
		// // mv.addObject("entity", entity);
		// if (loginUser  != null) {
		// 	mv.addObject("employeeId", loginUser.getEmployee_id());
			
		// 	// session.setAttribute("loginUser", loginUser );
		// 	// session.setAttribute("userName", entity.getAccount());
		// 	// session.setAttribute("companyId", entity.getCompany_id());
		// 	// session.setAttribute("officeId", entity.getOffice_id());
		// } else {
		// 	mv.addObject("employeeId", 0);
		// 	// セッションに保持
		// 	// session.setAttribute("userName", userName);
		// 	switch (userName) {
		// 		case "osaka@kyouseibin.com":
		// 			session.setAttribute("companyId", 1000);
		// 			session.setAttribute("officeId", 1001);
		// 			break;
		// 		case "wakayama@kyouseibin.com":
		// 			session.setAttribute("companyId", 1000);
		// 			session.setAttribute("officeId", 1002);
		// 			break;
		// 		case "hyogo@kyouseibin.com":
		// 			session.setAttribute("companyId", 1000);
		// 			session.setAttribute("officeId", 1003);
		// 			break;
		// 		case null:
		// 			session.setAttribute("companyId", 1000);
		// 			session.setAttribute("officeId", 0);
		// 			break;
		// 		default:
		// 			session.setAttribute("companyId", 1000);
		// 			session.setAttribute("officeId", 0);
		// 			break;
		// 	}
		// }
        // EmployeeEntity loginUser = (EmployeeEntity) session.getAttribute("loginUser");
        // if (loginUser == null) {
        //     throw new IllegalStateException("セッション切れ");
        // }
        mv.addObject("user", loginUser);

		historyService.save(loginUser.getAccount(), "", "ログイン", 200, "ログインしました。");

        return mv;
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
