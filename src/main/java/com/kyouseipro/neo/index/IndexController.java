package com.kyouseipro.neo.index;

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

import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.personnel.employee.service.EmployeeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class IndexController {
	private final EmployeeService employeeService;

	/**
	 * スタートページ
	 * @param mv
	 * @return
	 */
	@GetMapping("/")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public String getIndex(Model model, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity loginUser = employeeService.getByAccount(userName);
			// .orElseThrow(() -> new IllegalStateException("ユーザーが存在しません"));
		if (loginUser == null) return null;

		// セッションに保持
		session.setAttribute("loginUser", loginUser);
        model.addAttribute("user", loginUser);

		model.addAttribute("title", "ホーム");
        model.addAttribute("insertCss", "/css/index/home.css");

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
    public void logOut(HttpServletRequest httpRequest, HttpServletResponse response, HttpSession session) throws IOException {
        httpRequest.getSession().invalidate();
        String endSessionEndpoint = "https://login.microsoftonline.com/common/oauth2/v2.0/logout";
		String redirectUrl = "https://www.kyouseipro.com/";

		response.sendRedirect(endSessionEndpoint + "?post_logout_redirect_uri=" + URLEncoder.encode(redirectUrl, "UTF-8"));
    }
}
