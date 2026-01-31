package com.kyouseipro.neo.corporation;

import java.io.IOException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kyouseipro.neo.abstracts.controller.BaseController;
import com.kyouseipro.neo.common.ComboBoxService;
import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.history.service.HistoryService;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.corporation.company.entity.CompanyEntity;
import com.kyouseipro.neo.corporation.company.entity.CompanyListEntity;
import com.kyouseipro.neo.corporation.company.service.CompanyListService;
import com.kyouseipro.neo.corporation.office.entity.OfficeEntity;
import com.kyouseipro.neo.corporation.office.entity.OfficeListEntity;
import com.kyouseipro.neo.corporation.office.service.OfficeListService;
import com.kyouseipro.neo.corporation.staff.entity.StaffEntity;
import com.kyouseipro.neo.corporation.staff.entity.StaffListEntity;
import com.kyouseipro.neo.corporation.staff.service.StaffListService;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeListEntity;
import com.kyouseipro.neo.personnel.employee.service.EmployeeListService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CorporationPageController extends BaseController {
    private final CompanyListService companyListService;
    private final OfficeListService officeListService;
    private final StaffListService staffListService;
    private final EmployeeListService employeeListService;
    private final ComboBoxService comboBoxService;
    private final HistoryService historyService;
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

		historyService.save(user.getAccount(), "client", "閲覧", 200, "");

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
        // 初期表示用Officeリスト取得
        List<OfficeListEntity> officeOrigin = officeListService.getClientList();
        model.addAttribute("officeOrigin", officeOrigin);
        // 初期表示用Staffリスト取得
        List<StaffListEntity> staffOrigin = staffListService.getList();
        model.addAttribute("staffOrigin", staffOrigin);

        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getClientList();
        model.addAttribute("companyComboList", companyComboList);
        List<OfficeListEntity> officeComboList = comboBoxService.getOfficeList();
        model.addAttribute("officeComboList", officeComboList);

        // 保存用コード
        model.addAttribute("categoryShipperCode", Enums.clientCategory.SHIPPER.getCode());
        model.addAttribute("categorySupplierCode", Enums.clientCategory.SUPPLIER.getCode());
        model.addAttribute("categoryServiceCode", Enums.clientCategory.SERVICE.getCode());
        model.addAttribute("categoryTransportCode", Enums.clientCategory.TRANSPORT.getCode());
		
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

		historyService.save(user.getAccount(), "partner", "閲覧", 200, "");

        model.addAttribute("title", "パートナー");
		model.addAttribute("sidebarFragmentName", "~{fragments/common/menu :: registFragment}");
		model.addAttribute("activeMenu", "regist");
        model.addAttribute("activeSidebar", "partner");
        model.addAttribute("insertCss", "/css/corporation/partner.css");

        // 初期化されたエンティティ
        model.addAttribute("companyEntity", new CompanyEntity());
        model.addAttribute("staffEntity", new StaffEntity());

        // 初期表示用Clientリスト取得
        List<CompanyListEntity> companyOrigin = companyListService.getListByCategoryId(Enums.clientCategory.PARTNER.getCode());
        model.addAttribute("companyOrigin", companyOrigin);
        // 初期表示用Staffリスト取得
        List<EmployeeListEntity> staffOrigin = employeeListService.getList();
        model.addAttribute("staffOrigin", staffOrigin);

        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getCompanyListByCategory(Enums.clientCategory.PARTNER.getCode());
        model.addAttribute("companyComboList", companyComboList);

        model.addAttribute("url", "/company/get/id");
        model.addAttribute("owner_category", Enums.clientCategory.PARTNER.getCode());

        // 保存用コード
        model.addAttribute("categoryPartnerCode", Enums.clientCategory.PARTNER.getCode());
        model.addAttribute("categoryConstructCode", Enums.employeeCategory.CONSTRUCT.getCode());

        return "contents/corporation/partner";
    }
}
