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
import com.kyouseipro.neo.service.document.HistoryService;
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
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getIndex(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
		mv.setViewName("layouts/main");
		mv.addObject("title", "ホーム");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: homeFragment");
        mv.addObject("bodyFragmentName", "contents/index/home :: bodyFragment");
		mv.addObject("insertCss", "/css/home.css");
		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity entity = (EmployeeEntity) employeeService.getEmployeeByAccount(userName);
		mv.addObject("entity", entity);
		if (entity != null) {
			mv.addObject("employeeId", entity.getEmployee_id());
			// セッションに保持
			session.setAttribute("userName", entity.getAccount());
			session.setAttribute("companyId", entity.getCompany_id());
			session.setAttribute("officeId", entity.getOffice_id());
		} else {
			mv.addObject("employeeId", 0);
			// セッションに保持
			session.setAttribute("userName", userName);
			switch (userName) {
				case "osaka@kyouseibin.com":
					session.setAttribute("companyId", 1000);
					session.setAttribute("officeId", 1001);
					break;
				case "wakayama@kyouseibin.com":
					session.setAttribute("companyId", 1000);
					session.setAttribute("officeId", 1002);
					break;
				case "hyogo@kyouseibin.com":
					session.setAttribute("companyId", 1000);
					session.setAttribute("officeId", 1003);
					break;
				default:
					session.setAttribute("companyId", 1000);
					session.setAttribute("officeId", 0);
					break;
			}
		}
		historyService.saveHistory(userName, "", "ログイン", 200, "ログインしました。");
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
    public void logOut(HttpServletRequest httpRequest, HttpServletResponse response, @AuthenticationPrincipal OidcUser principal) throws IOException {
        httpRequest.getSession().invalidate();
        String endSessionEndpoint = "https://login.microsoftonline.com/common/oauth2/v2.0/logout";
		String redirectUrl = "https://www.kyouseipro.com/";
		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		historyService.saveHistory(userName, "", "ログアウト", 200, "ログアウトしました。");

		response.sendRedirect(endSessionEndpoint + "?post_logout_redirect_uri=" + URLEncoder.encode(redirectUrl, "UTF-8"));
    }

	/**
	 * 営業
	 * @param mv
	 * @return
	 */
	@GetMapping("/sales")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getSales(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
		mv.addObject("title", "営業");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: salesFragment");
        mv.addObject("bodyFragmentName", "contents/index/sales :: bodyFragment");
        mv.addObject("insertCss", "/css/sales/sales.css");
		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity entity = (EmployeeEntity) employeeService.getEmployeeByAccount(userName);
		mv.addObject("entity", entity);

		historyService.saveHistory(userName, "sales", "閲覧", 200, "");
		
        return mv;
    }

	/**
	 * 人事
	 * @param mv
	 * @return
	 */
	@GetMapping("/personnel")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getPersonnel(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
		mv.addObject("title", "人事");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: personnelFragment");
        mv.addObject("bodyFragmentName", "contents/index/personnel :: bodyFragment");
        mv.addObject("insertCss", "/css/personnel/personnel.css");
		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity entity = (EmployeeEntity) employeeService.getEmployeeByAccount(userName);
		mv.addObject("entity", entity);

		historyService.saveHistory(userName, "personnel", "閲覧", 200, "");
		
        return mv;
    }
}
