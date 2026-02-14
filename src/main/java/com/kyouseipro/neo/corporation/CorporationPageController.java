package com.kyouseipro.neo.corporation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kyouseipro.neo.abstracts.controller.BaseController;
import com.kyouseipro.neo.common.ComboBoxService;
import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.corporation.company.entity.CompanyEntity;
import com.kyouseipro.neo.corporation.company.entity.CompanyListEntity;
import com.kyouseipro.neo.corporation.company.service.CompanyListService;
import com.kyouseipro.neo.corporation.office.entity.OfficeEntity;
import com.kyouseipro.neo.corporation.staff.entity.StaffEntity;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CorporationPageController extends BaseController {
    private final CompanyListService companyListService;
    private final ComboBoxService comboBoxService;
    /**
	 * 取引先
	 * @param mv
	 * @return
	 */
	@GetMapping("/client")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getClient(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

        model.addAttribute("title", "取引先");
		model.addAttribute("activeMenu", "regist");
        model.addAttribute("activeSidebar", "client");
        model.addAttribute("insertCss", "/css/corporation/client.css");

        // 初期化されたエンティティ
        model.addAttribute("companyEntity", new CompanyEntity());
        model.addAttribute("officeEntity", new OfficeEntity());
        model.addAttribute("staffEntity", new StaffEntity());

        // 初期表示用Clientリスト取得
        List<CompanyListEntity> companyOrigin = companyListService.getClientList();
        model.addAttribute("companyOrigin", companyOrigin);

        // 保存用コード
        Map<String, Integer> categoryCodes = Arrays.stream(Enums.clientCategory.values())
            .collect(Collectors.toMap(
                Enums.clientCategory::name,
                Enums.clientCategory::getCode
            ));
        model.addAttribute("categoryCodes", categoryCodes);
		
        return "contents/corporation/client";
    }

    /**
	 * パートナー
	 * @param mv
	 * @return
	 */
	@GetMapping("/partner")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getPartner(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

        model.addAttribute("title", "パートナー");
		model.addAttribute("sidebarFragmentName", "~{fragments/common/menu :: registFragment}");
		model.addAttribute("activeMenu", "regist");
        model.addAttribute("activeSidebar", "partner");
        model.addAttribute("insertCss", "/css/corporation/partner.css");

        // 初期化されたエンティティ
        model.addAttribute("companyEntity", new CompanyEntity());
        model.addAttribute("staffEntity", new EmployeeEntity());

        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getCompanyListByCategory(Enums.clientCategory.PARTNER.getCode());
        model.addAttribute("companyComboList", companyComboList);
        List<SimpleData> genderComboList = comboBoxService.getGender();
        model.addAttribute("genderComboList", genderComboList);
        List<SimpleData> bloodTypeComboList = comboBoxService.getBloodType();
        model.addAttribute("bloodTypeComboList", bloodTypeComboList);

        // 保存用コード
        Map<String, Integer> categoryCodes = Map.of(
            "PARTNER", Enums.clientCategory.PARTNER.getCode(),
            "CONSTRUCT", Enums.employeeCategory.CONSTRUCT.getCode()
        );

        model.addAttribute("categoryCodes", categoryCodes);

        return "contents/corporation/partner";
    }
}
