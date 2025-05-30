package com.kyouseipro.neo.controller.page;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.entity.common.QualificationsEntity;
import com.kyouseipro.neo.entity.person.EmployeeEntity;
import com.kyouseipro.neo.interfaceis.Entity;
import com.kyouseipro.neo.service.ComboBoxService;
import com.kyouseipro.neo.service.DatabaseService;
import com.kyouseipro.neo.service.common.QualificationsService;
import com.kyouseipro.neo.service.personnel.EmployeeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class QualificationsPageController {
    private final EmployeeService employeeService;
    private final ComboBoxService comboBoxService;
    private final DatabaseService databaseService;
    private final QualificationsService qualificationsService;

    /**
	 * 資格
	 * @param mv
	 * @return
	 */
	@GetMapping("/qualifications/{type}")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getQualifications(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, @PathVariable String type) {

		mv.setViewName("layouts/main");
        mv.addObject("title", "資格");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
        mv.addObject("bodyFragmentName", "contents/common/qualifications :: bodyFragment");
        mv.addObject("insertCss", "/css/common/qualifications.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = (EmployeeEntity) employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("formEntity", new QualificationsEntity());

        switch (type.toLowerCase()) {
            case "employee":
                mv.addObject("sidebarFragmentName", "fragments/menu :: personnelFragment");
                // 初期表示用資格情報リスト取得
                List<Entity> qualificationsOrigin = qualificationsService.getEmployeeQualificationsList();
                mv.addObject("origin", qualificationsOrigin);
                // コンボボックスアイテム取得
                List<Entity> qualificationComboList = comboBoxService.getQualificationMaster();
                mv.addObject("qualificationComboList", qualificationComboList);
                break;
            case "company":
                mv.addObject("sidebarFragmentName", "fragments/menu :: salesFragment");
                // 初期表示用資格情報リスト取得
                List<Entity> licensesOrigin = qualificationsService.getCompanyQualificationsList();
                mv.addObject("origin", licensesOrigin);
                // コンボボックスアイテム取得
                List<Entity> licenseComboList = comboBoxService.getLicenseMaster();
                mv.addObject("qualificationComboList", licenseComboList);
                break;
            default:
                break;
        }

        // 履歴保存
        databaseService.saveHistory(userName, "qualifications", "閲覧", 200, "");
		
        return mv;
    }
}
