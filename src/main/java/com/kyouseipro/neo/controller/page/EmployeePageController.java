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
import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.personnel.EmployeeListEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.personnel.EmployeeListService;
import com.kyouseipro.neo.service.personnel.EmployeeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmployeePageController {
    private final EmployeeService employeeService;
    private final EmployeeListService employeeListService;
    private final ComboBoxService comboBoxService;
    private final HistoryService historyService;

    /**
	 * 従業員
	 * @param mv
	 * @return
	 */
	@GetMapping("/employee")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getEmployee(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "従業員");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: personnelFragment");
        mv.addObject("bodyFragmentName", "contents/personnel/employee :: bodyFragment");
        mv.addObject("insertCss", "/css/personnel/employee.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("formEntity", new EmployeeEntity());

        // 初期表示用従業員リスト取得
        List<EmployeeListEntity> origin = employeeListService.getEmployeeList();
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getOwnCompanyList();
        mv.addObject("companyComboList", companyComboList);
        List<SimpleData> clientComboList = comboBoxService.getClientList();
        mv.addObject("clientComboList", clientComboList);
        List<OfficeListEntity> officeList = comboBoxService.getOfficeList();
        mv.addObject("officeList", officeList);
        List<SimpleData> employeeCategoryComboList = comboBoxService.getEmployeeCategory();
        mv.addObject("employeeCategoryComboList", employeeCategoryComboList);
        List<SimpleData> genderComboList = comboBoxService.getGender();
        mv.addObject("genderComboList", genderComboList);
        List<SimpleData> bloodTypeComboList = comboBoxService.getBloodType();
        mv.addObject("bloodTypeComboList", bloodTypeComboList);
        List<SimpleData> paymentMethodComboList = comboBoxService.getPaymentMethod();
        mv.addObject("paymentMethodComboList", paymentMethodComboList);
        List<SimpleData> payTypeComboList = comboBoxService.getPayType();
        mv.addObject("payTypeComboList", payTypeComboList);

        // 保存用コード
        mv.addObject("categoryEmployeeCode", Enums.employeeCategory.FULLTIME.getCode());
        mv.addObject("categoryParttimeCode", Enums.employeeCategory.PARTTIME.getCode());
        mv.addObject("categoryConstructCode", Enums.employeeCategory.CONSTRUCT.getCode());

        // 履歴保存
        historyService.saveHistory(userName, "employee", "閲覧", 0, "");
		
        return mv;
    }
}
