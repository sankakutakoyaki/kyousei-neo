package com.kyouseipro.neo.controller.page;

import java.io.IOException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.controller.abstracts.BaseController;
import com.kyouseipro.neo.entity.dto.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.work.WorkItemEntity;
import com.kyouseipro.neo.entity.work.WorkPriceEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.dto.HistoryService;
import com.kyouseipro.neo.service.work.WorkItemService;
import com.kyouseipro.neo.service.work.WorkPriceService;

import jakarta.servlet.http.HttpServletResponse;
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
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getWorkItem(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

		historyService.save(user.getAccount(), "workitem", "閲覧", 200, "");

        model.addAttribute("title", "分類");
		model.addAttribute("activeMenu", "regist");
        model.addAttribute("activeSidebar", "item");
        model.addAttribute("insertCss", "/css/work/workitem.css");

        // 初期表示用受注リスト取得
        List<WorkItemEntity> origin = workItemService.getList();
        model.addAttribute("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> categoryComboList = comboBoxService.getWorkItemParentCategoryList();
        model.addAttribute("categoryComboList", categoryComboList);

        model.addAttribute("ownCompanyId", Utilities.getOwnCompanyId());
		
        return "contents/work/workitem";
    }

    @GetMapping("/work/price")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getWorkPrice(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

		historyService.save(user.getAccount(), "workprice", "閲覧", 200, "");

        model.addAttribute("title", "料金表");
		model.addAttribute("activeMenu", "regist");
        model.addAttribute("activeSidebar", "price");
        model.addAttribute("insertCss", "/css/work/workprice.css");

        // 初期表示用受注リスト取得
        List<WorkPriceEntity> origin = workPriceService.getListByCompanyId(Utilities.getOwnCompanyId());
        model.addAttribute("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getPrimeConstractorListAddTopOfOwnCompanyHasOriginalPrice();
        model.addAttribute("companyComboList", companyComboList);
        List<SimpleData> categoryComboList = comboBoxService.getWorkItemParentCategoryList();
        model.addAttribute("categoryComboList", categoryComboList);
		
        return "contents/work/workprice";
    }
}
