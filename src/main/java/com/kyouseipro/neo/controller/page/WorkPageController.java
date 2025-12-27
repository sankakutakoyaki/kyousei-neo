package com.kyouseipro.neo.controller.page;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.work.WorkItemEntity;
import com.kyouseipro.neo.entity.work.WorkPriceEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.personnel.EmployeeService;
import com.kyouseipro.neo.service.regist.WorkItemService;
import com.kyouseipro.neo.service.regist.WorkPriceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkPageController {
    private final EmployeeService employeeService;
    private final WorkItemService workItemService;
    private final WorkPriceService workPriceService;
    private final ComboBoxService comboBoxService;
    private final HistoryService historyService;

    @GetMapping("/work/item")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getWorkItem(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "作業項目");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: registFragment");
        mv.addObject("bodyFragmentName", "contents/regist/workitem :: bodyFragment");
        mv.addObject("insertCss", "/css/regist/workitem.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期表示用受注リスト取得
        List<WorkItemEntity> origin = workItemService.getList();
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> categoryComboList = comboBoxService.getWorkItemParentCategoryList();
        mv.addObject("categoryComboList", categoryComboList);

        mv.addObject("ownCompanyId", Utilities.getOwnCompanyId());

        // 履歴保存
        historyService.saveHistory(userName, "work_item", "閲覧", 0, "");
		
        return mv;
    }

    @GetMapping("/work/price")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getWorkPrice(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "作業料金");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: registFragment");
        mv.addObject("bodyFragmentName", "contents/regist/workprice :: bodyFragment");
        mv.addObject("insertCss", "/css/regist/workprice.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期表示用受注リスト取得
        List<WorkPriceEntity> origin = workPriceService.getListByCompanyId(Utilities.getOwnCompanyId());
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getPrimeConstractorListAddTopOfOwnCompanyHasOriginalPrice();
        mv.addObject("companyComboList", companyComboList);
        List<SimpleData> categoryComboList = comboBoxService.getWorkItemParentCategoryList();
        mv.addObject("categoryComboList", categoryComboList);

        // 履歴保存
        historyService.saveHistory(userName, "work_price", "閲覧", 0, "");
		
        return mv;
    }
}
