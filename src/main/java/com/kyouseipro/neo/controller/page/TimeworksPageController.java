package com.kyouseipro.neo.controller.page;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;
import com.kyouseipro.neo.service.DatabaseService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TimeworksPageController {
    private final DatabaseService databaseService;
    
    /**
     * 打刻一覧画面を呼び出す
     * @param mv
     * @param token
     * @return ModelAndView
     */
	@GetMapping("/timeworks")
	public ModelAndView showList(ModelAndView mv, OAuth2AuthenticationToken token, @AuthenticationPrincipal OidcUser principal) {
        mv.setViewName("layouts/main");
        mv.addObject("title", "勤怠");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: personnelFragment");
        mv.addObject("bodyFragmentName", "contents/personnel/timeworks :: bodyFragment");
        mv.addObject("insertCss", "/css/personnel/timeworks.css");

        // ユーザー名
        String userName = principal.getAttribute("preferred_username");
        mv.addObject("username", userName);
        // MVを設定
		
        // 勤怠データ新規・更新用
        TimeworksListEntity entity = new TimeworksListEntity();
        mv.addObject("entity", entity);

        // 履歴保存
        databaseService.saveHistory(userName, "timeworks", "閲覧", 200, "");
	    return mv;
	}
}
