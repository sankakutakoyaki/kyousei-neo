package com.kyouseipro.neo.controller.page;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsListEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.personnel.EmployeeService;
import com.kyouseipro.neo.service.personnel.WorkingConditionsListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkingConditionsPageController {
    private final EmployeeService employeeService;
    private final WorkingConditionsListService workingConditionsListService;
    private final ComboBoxService comboBoxService;
    private final HistoryService historyService;

    /**
	 * 勤務条件
	 * @param mv
	 * @return
	 */
	@GetMapping("/working_conditions")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getWorkingConditions(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "勤務条件");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: personnelFragment");
        mv.addObject("bodyFragmentName", "contents/personnel/working_conditions :: bodyFragment");
        mv.addObject("insertCss", "/css/personnel/working_conditions.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = (EmployeeEntity) employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("formEntity", new WorkingConditionsEntity());

        // 初期表示用従業員リスト取得
        List<WorkingConditionsListEntity> origin = workingConditionsListService.getWorkingConditionsList();
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> paymentMethodComboList = comboBoxService.getPaymentMethod();
        mv.addObject("paymentMethodComboList", paymentMethodComboList);
        List<SimpleData> payTypeComboList = comboBoxService.getPayType();
        mv.addObject("payTypeComboList", payTypeComboList);

        // 保存用コード
        mv.addObject("categoryEmployeeCode", Enums.employeeCategory.FULLTIME.getCode());
        mv.addObject("categoryParttimeCode", Enums.employeeCategory.PARTTIME.getCode());

        // 履歴保存
        historyService.saveHistory(userName, "working_conditions", "閲覧", 200, "");
		
        return mv;
    }
}
