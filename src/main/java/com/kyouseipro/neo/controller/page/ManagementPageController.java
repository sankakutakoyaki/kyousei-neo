package com.kyouseipro.neo.controller.page;

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
import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.management.qualification.QualificationsEntity;
import com.kyouseipro.neo.entity.management.recycle.RecycleEntity;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.management.qualification.QualificationsService;
import com.kyouseipro.neo.service.management.recycle.RecycleService;
import com.kyouseipro.neo.service.personnel.EmployeeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ManagementPageController {
    private final EmployeeService employeeService;
    private final RecycleService recycleService;
    private final HistoryService historyService;
    private final ComboBoxService comboBoxService;
    private final QualificationsService qualificationsService;

	@GetMapping("/recycle")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getOrder(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "登録");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: managementFragment");
        mv.addObject("bodyFragmentName", "contents/management/recycle :: bodyFragment");
        mv.addObject("insertCss", "/css/management/recycle.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("formEntity", new RecycleEntity());

        // 初期表示用リスト取得
        List<RecycleEntity> origin = recycleService.getBetweenRecycleEntity(LocalDate.now(), LocalDate.now(), "regist");
        mv.addObject("origin", origin);
        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getPrimeConstractorList();
        // 自社を取得
        List<SimpleData> ownList = comboBoxService.getOwnCompanyList();
        // 自社を先頭に追加
        for (SimpleData simpleData : ownList) {
            companyComboList.add(0, simpleData);
        }
        mv.addObject("companyComboList", companyComboList);
        // 支店リストを取得
        List<OfficeListEntity> officeComboList = comboBoxService.getOfficeList();
        mv.addObject("officeComboList", officeComboList);

        mv.addObject("deleteCode", Enums.state.DELETE.getCode());

        // 履歴保存
        historyService.saveHistory(userName, "recycle", "閲覧", 0, "");
		
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
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
        mv.addObject("sidebarFragmentName", "fragments/menu :: managementFragment");
        mv.addObject("bodyFragmentName", "contents/management/qualifications :: bodyFragment");
        mv.addObject("insertCss", "/css/management/qualifications.css");

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

        // mv.addObject("url", "/license/get/id");
        mv.addObject("url", "/company/get/id");
        mv.addObject("owner_category", Enums.clientCategory.PARTNER.getCode());
        // 履歴保存
        historyService.saveHistory(userName, "qualifications", "閲覧", 200, "");
		
        return mv;
    }
}
