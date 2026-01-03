package com.kyouseipro.neo.controller.page;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.controller.abstracts.BaseController;
import com.kyouseipro.neo.entity.ks.KsSalesEntity;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.ks.KsSalesService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class KsPageController extends BaseController {
    // private final EmployeeService employeeService;
    private final KsSalesService ksSalesService;
    private final HistoryService historyService;

    @GetMapping("/ks/sales")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getKsSales(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
		mv.setViewName("layouts/main");
        mv.addObject("title", "ケーズ");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: salesFragment");
        mv.addObject("bodyFragmentName", "contents/ks/ks :: bodyFragment");
        mv.addObject("insertCss", "/css/ks/ks.css");

        EmployeeEntity user = getLoginUser(session, response);
        if (user == null) return null; // リダイレクト済みなので処理は止まる
		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity user = employeeService.getByAccount(userName);
		// mv.addObject("user", user);
        // mv.addObject("user", employeeService.getByAccount(userName).orElse(null));

        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd   = now.withDayOfMonth(now.lengthOfMonth());
        // 初期表示用受注リスト取得
        List<KsSalesEntity> origin01 = ksSalesService.getAllFromBetween(monthStart, monthEnd.plusDays(1), "staff");
        mv.addObject("origin01", origin01);

        List<String> storeNames = origin01.stream().map(KsSalesEntity::getStore_name).filter(Objects::nonNull).distinct().toList();
        mv.addObject("storeComboList01", storeNames);

        // EmployeeEntity user = getLoginUser(session);
        historyService.save(user.getAccount(), "ks_sales", "閲覧", 0, "");
		
        return mv;
    }
}
