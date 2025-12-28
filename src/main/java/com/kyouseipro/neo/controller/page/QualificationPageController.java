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
import com.kyouseipro.neo.entity.qualification.QualificationsEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.personnel.EmployeeService;
import com.kyouseipro.neo.service.qualification.QualificationsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class QualificationPageController {
    private final EmployeeService employeeService;
    private final HistoryService historyService;
    private final ComboBoxService comboBoxService;
    private final QualificationsService qualificationsService;

    /**
	 * 資格
	 * @param mv
	 * @return
	 */
	@GetMapping("/qualifications")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getQualifications(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "資格");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
        mv.addObject("sidebarFragmentName", "fragments/common/menu :: personnelFragment");
        mv.addObject("bodyFragmentName", "contents/qualifications/qualifications :: bodyFragment");
        mv.addObject("insertCss", "/css/qualifications/qualifications.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = (EmployeeEntity) employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("formEntity", new QualificationsEntity());
        // 初期表示用資格情報リスト取得
        List<QualificationsEntity> qualificationsOrigin = qualificationsService.getEmployeeQualificationsList();
        mv.addObject("origin", qualificationsOrigin);
        // コンボボックスアイテム取得
        List<SimpleData> qualificationComboList = comboBoxService.getQualificationMaster();
        mv.addObject("qualificationComboList", qualificationComboList);

        mv.addObject("url", "/employee/get/id");
        mv.addObject("owner_category", 0);
        // 履歴保存
        historyService.saveHistory(userName, "qualifications", "閲覧", 200, "");
		
        return mv;
    }

    /**
	 * 許認可
	 * @param mv
	 * @return
	 */
	@GetMapping("/license")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getLicense(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "許認可");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
        mv.addObject("sidebarFragmentName", "fragments/common/menu :: managementFragment");
        mv.addObject("bodyFragmentName", "contents/qualifications/qualifications :: bodyFragment");
        mv.addObject("insertCss", "/css/qualifications/qualifications.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = (EmployeeEntity) employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("formEntity", new QualificationsEntity());

        // 初期表示用資格情報リスト取得
        List<QualificationsEntity> licensesOrigin = qualificationsService.getCompanyQualificationsList();
        mv.addObject("origin", licensesOrigin);
        // コンボボックスアイテム取得
        List<SimpleData> licenseComboList = comboBoxService.getLicenseMaster();
        mv.addObject("qualificationComboList", licenseComboList);

        mv.addObject("url", "/company/get/id");
        mv.addObject("owner_category", Enums.clientCategory.PARTNER.getCode());
        // 履歴保存
        historyService.saveHistory(userName, "qualifications", "閲覧", 200, "");
		
        return mv;
    }
}
