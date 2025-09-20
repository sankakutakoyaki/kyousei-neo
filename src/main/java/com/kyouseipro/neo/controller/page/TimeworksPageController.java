package com.kyouseipro.neo.controller.page;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeListEntity;
import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.personnel.EmployeeListService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TimeworksPageController {
    private final HistoryService historyService;
    private final ComboBoxService comboBoxService;
    private final EmployeeListService employeeListService;
    
    /**
     * 打刻一覧画面を呼び出す
     * @param mv
     * @param token
     * @return ModelAndView
     */
	@GetMapping("/timeworks")
	public ModelAndView showList(ModelAndView mv, OAuth2AuthenticationToken token, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
        mv.setViewName("layouts/main");
        mv.addObject("title", "勤怠");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: personnelFragment");
        mv.addObject("bodyFragmentName", "contents/personnel/timeworks :: bodyFragment");
        mv.addObject("insertCss", "/css/personnel/timeworks.css");

        // ユーザー名
        String userName = principal.getAttribute("preferred_username");
        mv.addObject("username", userName);
        // 勤怠データ新規・更新用
        TimeworksListEntity entity = new TimeworksListEntity();
        mv.addObject("entity", entity);
        // 初期表示用営業所ID
        mv.addObject("officeId", session.getAttribute("officeId") == null ? "1000" : session.getAttribute("officeId").toString());
        // 営業所リスト
        List<SimpleData> officeList = comboBoxService.getSimpleOfficeList();
        mv.addObject("officeList", officeList);
        // 初期表示用従業員リスト取得
        List<EmployeeListEntity> employeeList = employeeListService.getEmployeeList();
        mv.addObject("employeeList", employeeList);
        // 完了コードを取得
        mv.addObject("completeNum", Enums.state.COMPLETE.getCode());
        // 履歴保存
        historyService.saveHistory(userName, "timeworks", "閲覧", 200, "");
	    return mv;
	}
}
