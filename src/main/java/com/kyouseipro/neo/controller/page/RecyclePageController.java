package com.kyouseipro.neo.controller.page;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.controller.abstracts.BaseController;
import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.recycle.RecycleEntity;
import com.kyouseipro.neo.entity.recycle.RecycleMakerEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.recycle.RecycleMakerService;
import com.kyouseipro.neo.service.recycle.RecycleService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecyclePageController extends BaseController {
    private final RecycleService recycleService;
    private final RecycleMakerService recycleMakerService;
    private final HistoryService historyService;
    private final ComboBoxService comboBoxService;

	@GetMapping("/recycle/regist")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getRecycle(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
		mv.setViewName("layouts/main");
        mv.addObject("title", "登録");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: recycleFragment");
        mv.addObject("bodyFragmentName", "contents/recycle/recycle :: bodyFragment");
        mv.addObject("insertCss", "/css/recycle/recycle.css");

        EmployeeEntity user = getLoginUser(session, response);
        if (user == null) return null; // リダイレクト済みなので処理は止まる
		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity user = employeeService.getByAccount(userName);
		// mv.addObject("user", user);
        // mv.addObject("user", employeeService.getByAccount(userName).orElse(null));

        // 初期化されたエンティティ
        mv.addObject("formEntity", new RecycleEntity());

        // 初期表示用リスト取得
        List<RecycleEntity> origin = recycleService.getBetween(LocalDate.now(), LocalDate.now(), "regist");
        mv.addObject("origin", origin);
        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getPrimeConstractorListAddTopOfOwnCompany();
        mv.addObject("companyComboList", companyComboList);
        // 支店リストを取得
        List<OfficeListEntity> officeComboList = comboBoxService.getOfficeList();
        mv.addObject("officeComboList", officeComboList);

        mv.addObject("deleteCode", Enums.state.DELETE.getCode());

        // EmployeeEntity user = getLoginUser(session);
        historyService.save(user.getAccount(), "recycle", "閲覧", 0, "");
		
        return mv;
    }

	@GetMapping("/recycle/maker")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getMaker(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
		mv.setViewName("layouts/main");
        mv.addObject("title", "登録");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: recycleFragment");
        mv.addObject("bodyFragmentName", "contents/recycle/maker :: bodyFragment");
        mv.addObject("insertCss", "/css/recycle/maker.css");

        EmployeeEntity user = getLoginUser(session, response);
        if (user == null) return null; // リダイレクト済みなので処理は止まる

        // 初期化されたエンティティ
        mv.addObject("formEntity", new RecycleMakerEntity());

        // 初期表示用リスト取得
        List<RecycleMakerEntity> origin = recycleMakerService.getList();
        mv.addObject("origin", origin);
        // コンボボックスアイテム取得
        List<SimpleData> groupComboList = comboBoxService.getRecycleGroupList();
        mv.addObject("groupComboList", groupComboList);

        mv.addObject("otherCode", 3);

        historyService.save(user.getAccount(), "recycle", "閲覧", 0, "");
		
        return mv;
    }
}
