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
import com.kyouseipro.neo.controller.abstracts.BaseController;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.work.WorkItemEntity;
import com.kyouseipro.neo.entity.work.WorkPriceEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.work.WorkItemService;
import com.kyouseipro.neo.service.work.WorkPriceService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkPageController extends BaseController {
    private final WorkItemService workItemService;
    private final WorkPriceService workPriceService;
    private final ComboBoxService comboBoxService;
    private final HistoryService historyService;

    @GetMapping("/work/item")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getWorkItem(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "作業項目");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: registFragment");
        mv.addObject("bodyFragmentName", "contents/work/workitem :: bodyFragment");
        mv.addObject("insertCss", "/css/work/workitem.css");

		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity user = employeeService.getByAccount(userName);
		// mv.addObject("user", user);
        // mv.addObject("user", employeeService.getByAccount(userName).orElse(null));

        // 初期表示用受注リスト取得
        List<WorkItemEntity> origin = workItemService.getList();
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> categoryComboList = comboBoxService.getWorkItemParentCategoryList();
        mv.addObject("categoryComboList", categoryComboList);

        mv.addObject("ownCompanyId", Utilities.getOwnCompanyId());

        EmployeeEntity user = getLoginUser(session);
        historyService.save(user.getAccount(), "work_item", "閲覧", 0, "");
		
        return mv;
    }

    @GetMapping("/work/price")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getWorkPrice(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "作業料金");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: registFragment");
        mv.addObject("bodyFragmentName", "contents/work/workprice :: bodyFragment");
        mv.addObject("insertCss", "/css/work/workprice.css");

		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity user = employeeService.getByAccount(userName);
		// mv.addObject("user", user);
        // mv.addObject("user", employeeService.getByAccount(userName).orElse(null));

        // 初期表示用受注リスト取得
        List<WorkPriceEntity> origin = workPriceService.getListByCompanyId(Utilities.getOwnCompanyId());
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getPrimeConstractorListAddTopOfOwnCompanyHasOriginalPrice();
        mv.addObject("companyComboList", companyComboList);
        List<SimpleData> categoryComboList = comboBoxService.getWorkItemParentCategoryList();
        mv.addObject("categoryComboList", categoryComboList);

        EmployeeEntity user = getLoginUser(session);
        historyService.save(user.getAccount(), "work_price", "閲覧", 0, "");
		
        return mv;
    }
}
