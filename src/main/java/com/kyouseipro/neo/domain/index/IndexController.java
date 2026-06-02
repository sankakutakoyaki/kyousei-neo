package com.kyouseipro.neo.domain.index;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class IndexController {
	
	/**
	 * スタートページ
	 * @param mv
	 * @return
	 */
	@GetMapping("/")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public String getIndex() {
		return "fragments/pages/index/home";
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
