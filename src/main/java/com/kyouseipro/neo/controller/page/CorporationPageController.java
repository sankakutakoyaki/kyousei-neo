package com.kyouseipro.neo.controller.page;

import java.io.IOException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.controller.abstracts.BaseController;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.CompanyListEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.corporation.StaffEntity;
import com.kyouseipro.neo.entity.corporation.StaffListEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.personnel.EmployeeListEntity;
import com.kyouseipro.neo.entity.qualification.QualificationsEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.corporation.CompanyListService;
import com.kyouseipro.neo.service.corporation.OfficeListService;
import com.kyouseipro.neo.service.corporation.StaffListService;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.personnel.EmployeeListService;
import com.kyouseipro.neo.service.qualification.QualificationsService;

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
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getClient(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
		mv.setViewName("layouts/main");
        mv.addObject("title", "取引先");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: registFragment");
        mv.addObject("bodyFragmentName", "contents/corporation/client :: bodyFragment");
        mv.addObject("insertCss", "/css/corporation/client.css");

        EmployeeEntity user = getLoginUser(session, response);
        if (user == null) return null; // リダイレクト済みなので処理は止まる
		// // ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// mv.addObject("user", employeeService.getByAccount(userName).orElse(null));

        // 初期化されたエンティティ
        mv.addObject("companyEntity", new CompanyEntity());
        mv.addObject("officeEntity", new OfficeEntity());
        mv.addObject("staffEntity", new StaffEntity());

        // 初期表示用Clientリスト取得
        List<CompanyListEntity> companyOrigin = companyListService.getClientList();
        // List<CompanyListEntity> companyOrigin = companyListService.getListByCategoryId(Enums.clientCategory.SHIPPER.getCode());
        mv.addObject("companyOrigin", companyOrigin);
        // 初期表示用Officeリスト取得
        List<OfficeListEntity> officeOrigin = officeListService.getClientList();
        mv.addObject("officeOrigin", officeOrigin);
        // 初期表示用Staffリスト取得
        List<StaffListEntity> staffOrigin = staffListService.getList();
        mv.addObject("staffOrigin", staffOrigin);

        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getClientList();
        mv.addObject("companyComboList", companyComboList);
        List<OfficeListEntity> officeComboList = comboBoxService.getOfficeList();
        mv.addObject("officeComboList", officeComboList);

        // 保存用コード
        mv.addObject("categoryPartnerCode", Enums.clientCategory.PARTNER.getCode());
        mv.addObject("categoryShipperCode", Enums.clientCategory.SHIPPER.getCode());
        mv.addObject("categorySupplierCode", Enums.clientCategory.SUPPLIER.getCode());
        mv.addObject("categoryServiceCode", Enums.clientCategory.SERVICE.getCode());
        mv.addObject("categoryTransportCode", Enums.clientCategory.TRANSPORT.getCode());

        // EmployeeEntity user = getLoginUser(session);
        // // 履歴保存
        historyService.save(user.getAccount(), "companies", "閲覧", 200, "");

        // mv.addObject("user", user);
		
        return mv;
    }

    /**
	 * パートナー
	 * @param mv
	 * @return
	 */
	@GetMapping("/partner")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getPartner(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
		mv.setViewName("layouts/main");
        mv.addObject("title", "パートナー");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: registFragment");
        mv.addObject("bodyFragmentName", "contents/corporation/partner :: bodyFragment");
        mv.addObject("insertCss", "/css/corporation/partner.css");

        EmployeeEntity user = getLoginUser(session, response);
        if (user == null) return null; // リダイレクト済みなので処理は止まる

        // 初期化されたエンティティ
        mv.addObject("companyEntity", new CompanyEntity());
        mv.addObject("staffEntity", new StaffEntity());

        // 初期表示用Clientリスト取得
        List<CompanyListEntity> companyOrigin = companyListService.getListByCategoryId(Enums.clientCategory.PARTNER.getCode());
        mv.addObject("companyOrigin", companyOrigin);
        // 初期表示用Staffリスト取得
        List<EmployeeListEntity> staffOrigin = employeeListService.getListByCategoryId(Enums.employeeCategory.CONSTRUCT.getCode());
        mv.addObject("staffOrigin", staffOrigin);

        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getCompanyListByCategory(Enums.clientCategory.PARTNER.getCode());
        mv.addObject("companyComboList", companyComboList);

        mv.addObject("url", "/company/get/id");
        mv.addObject("owner_category", Enums.clientCategory.PARTNER.getCode());

        // 保存用コード
        mv.addObject("categoryPartnerCode", Enums.clientCategory.PARTNER.getCode());

        // 履歴保存
        historyService.save(user.getAccount(), "companies", "閲覧", 200, "");

        return mv;
    }
}
