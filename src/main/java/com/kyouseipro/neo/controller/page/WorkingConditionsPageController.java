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
import com.kyouseipro.neo.entity.person.EmployeeEntity;
import com.kyouseipro.neo.entity.person.WorkingConditionsEntity;
import com.kyouseipro.neo.interfaceis.Entity;
import com.kyouseipro.neo.service.ComboBoxService;
import com.kyouseipro.neo.service.DatabaseService;
import com.kyouseipro.neo.service.personnel.EmployeeService;
import com.kyouseipro.neo.service.personnel.WorkingConditionsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkingConditionsPageController {
    private final EmployeeService employeeService;
    private final WorkingConditionsService workingConditionsService;
    private final ComboBoxService comboBoxService;
    private final DatabaseService databaseService;

    /**
	 * 勤務条件
	 * @param mv
	 * @return
	 */
	@GetMapping("/working_conditions")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
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
        List<Entity> origin = workingConditionsService.getWorkingConditionsList();
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<Entity> paymentMethodComboList = comboBoxService.getPaymentMethod();
        mv.addObject("paymentMethodComboList", paymentMethodComboList);
        List<Entity> payTypeComboList = comboBoxService.getPayType();
        mv.addObject("payTypeComboList", payTypeComboList);

        // 保存用コード
        mv.addObject("categoryEmployeeCode", Enums.employeeCategory.FULLTIME.getCode());
        mv.addObject("categoryParttimeCode", Enums.employeeCategory.PARTTIME.getCode());

        // 履歴保存
        databaseService.saveHistory(userName, "working_conditions", "閲覧", 200, "");
		
        return mv;
    }
}
