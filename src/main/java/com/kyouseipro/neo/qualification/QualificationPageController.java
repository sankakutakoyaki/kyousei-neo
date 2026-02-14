package com.kyouseipro.neo.qualification;

import java.io.IOException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.abstracts.controller.BaseController;
import com.kyouseipro.neo.common.ComboBoxService;
import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.history.service.HistoryService;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.qualification.entity.QualificationsEntity;
import com.kyouseipro.neo.qualification.service.QualificationsService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class QualificationPageController extends BaseController {
    private final ComboBoxService comboBoxService;
    private final QualificationsService qualificationsService;

    /**
	 * 資格
	 * @param mv
	 * @return
	 */
	@GetMapping("/qualifications")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getQualifications(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

        model.addAttribute("title", "資格");
		model.addAttribute("activeMenu", "management");
        model.addAttribute("activeSidebar", "qualifications");
        model.addAttribute("insertCss", "/css/qualifications/qualifications.css");

        // 初期化されたエンティティ
        model.addAttribute("formEntity", new QualificationsEntity());
        // 初期表示用資格情報リスト取得
        List<QualificationsEntity> origin = qualificationsService.getListForEmployee();
        model.addAttribute("origin", origin);
        // コンボボックスアイテム取得
        List<SimpleData> qualificationComboList = comboBoxService.getQualificationMaster();
        model.addAttribute("qualificationComboList", qualificationComboList);

        model.addAttribute("url", "/employee/get/id");
        model.addAttribute("owner_category", 0);

        return "contents/qualifications/qualifications";
    }

    /**
	 * 許認可
	 * @param mv
	 * @return
	 */
	@GetMapping("/license")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public String getLicense(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

        model.addAttribute("title", "許認可");
		model.addAttribute("activeMenu", "management");
        model.addAttribute("activeSidebar", "license");
        model.addAttribute("insertCss", "/css/qualifications/qualifications.css");

        // 初期化されたエンティティ
        model.addAttribute("formEntity", new QualificationsEntity());

        // 初期表示用資格情報リスト取得
        List<QualificationsEntity> origin = qualificationsService.getListFroCompany();
        model.addAttribute("origin", origin);
        // コンボボックスアイテム取得
        List<SimpleData> licenseComboList = comboBoxService.getLicenseMaster();
        model.addAttribute("qualificationComboList", licenseComboList);

        model.addAttribute("url", "/company/get/id");
        model.addAttribute("owner_category", Enums.clientCategory.PARTNER.getCode());

        return "contents/qualifications/qualifications";
    }
}
