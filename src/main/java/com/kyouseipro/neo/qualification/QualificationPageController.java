package com.kyouseipro.neo.qualification;

import java.io.IOException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kyouseipro.neo.abstracts.controller.BaseController;
import com.kyouseipro.neo.common.ComboBoxService;
import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.qualification.entity.QualificationsEntityRequest;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class QualificationPageController extends BaseController {
    private final ComboBoxService comboBoxService;

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

        model.addAttribute("title", "許認可・資格");
		model.addAttribute("activeMenu", "management");
        model.addAttribute("activeSidebar", "qualifications");
        model.addAttribute("insertCss", "/css/qualifications/qualifications.css");

        // 初期化されたエンティティ
        model.addAttribute("formEntity", new QualificationsEntityRequest());
        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getCompanyListByCategory(Enums.clientCategory.PARTNER.getCode());
        model.addAttribute("companyComboList", companyComboList);
        List<SimpleData> qualificationComboList = comboBoxService.getQualificationMaster();
        model.addAttribute("qualificationsComboList", qualificationComboList);
        List<SimpleData> licenseComboList = comboBoxService.getLicenseMaster();
        model.addAttribute("licenseComboList", licenseComboList);

        return "contents/qualifications/qualifications";
    }
}
