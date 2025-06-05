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
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.CompanyListEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.corporation.StaffEntity;
import com.kyouseipro.neo.entity.corporation.StaffListEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.service.DatabaseService;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.corporation.CompanyListService;
import com.kyouseipro.neo.service.corporation.OfficeListService;
import com.kyouseipro.neo.service.corporation.StaffListService;
import com.kyouseipro.neo.service.corporation.StaffService;
import com.kyouseipro.neo.service.personnel.EmployeeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CompanyPageController {
    private final EmployeeService employeeService;
    private final CompanyListService companyListService;
    private final OfficeListService officeListService;
    private final StaffListService staffListService;
    private final ComboBoxService comboBoxService;
    private final DatabaseService databaseService;

    /**
	 * 取引先
	 * @param mv
	 * @return
	 */
	@GetMapping("/client")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getClient(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "取引先");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: salesFragment");
        mv.addObject("bodyFragmentName", "contents/sales/client :: bodyFragment");
        mv.addObject("insertCss", "/css/sales/client.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("companyEntity", new CompanyEntity());
        mv.addObject("officeEntity", new OfficeEntity());
        mv.addObject("staffEntity", new StaffEntity());

        // 初期表示用Clientリスト取得
        List<CompanyListEntity> companyOrigin = companyListService.getClientList();
        mv.addObject("companyOrigin", companyOrigin);
        // 初期表示用Officeリスト取得
        List<OfficeListEntity> officeOrigin = officeListService.getClientList();
        mv.addObject("officeOrigin", officeOrigin);
        // 初期表示用Staffリスト取得
        List<StaffListEntity> staffOrigin = staffListService.getStaffList();
        mv.addObject("staffOrigin", staffOrigin);

        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getClientList();
        mv.addObject("companyComboList", companyComboList);
        List<SimpleData> officeComboList = comboBoxService.getOfficeList();
        mv.addObject("officeComboList", officeComboList);

        // 保存用コード
        mv.addObject("categoryPartnerCode", Enums.clientCategory.PARTNER.getCode());
        mv.addObject("categoryShipperCode", Enums.clientCategory.SHIPPER.getCode());
        mv.addObject("categorySupplierCode", Enums.clientCategory.SUPPLIER.getCode());
        mv.addObject("categoryServiceCode", Enums.clientCategory.SERVICE.getCode());

        // 履歴保存
        databaseService.saveHistory(userName, "companies", "閲覧", 200, "");
		
        return mv;
    }
}
